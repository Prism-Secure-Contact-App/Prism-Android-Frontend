/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.datasource

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.features.home.impl.model.RoomListRoomSummary
import io.prism.android.libraries.androidutils.diff.DiffCacheUpdater
import io.prism.android.libraries.androidutils.diff.MutableListDiffCache
import io.prism.android.libraries.androidutils.system.DateTimeObserver
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.notificationsettings.NotificationSettingsService
import io.prism.android.libraries.matrix.api.roomlist.RoomList
import io.prism.android.libraries.matrix.api.roomlist.RoomListFilter
import io.prism.android.libraries.matrix.api.roomlist.RoomListService
import io.prism.android.libraries.matrix.api.roomlist.RoomSummary
import io.prism.android.libraries.matrix.api.roomlist.updateVisibleRange
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

private const val PAGE_SIZE = 20
private const val EXTENDED_VISIBILITY_RANGE_SIZE = 40
private const val SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS = 300L
private const val PAGINATION_THRESHOLD = 3 * PAGE_SIZE

@Inject
@SingleIn(SessionScope::class)
class RoomListDataSource(
    private val roomListService: RoomListService,
    private val roomListRoomSummaryFactory: RoomListRoomSummaryFactory,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val notificationSettingsService: NotificationSettingsService,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val dateTimeObserver: DateTimeObserver,
    private val analyticsService: AnalyticsService,
) {
    init {
        observeNotificationSettings()
        observeDateTimeChanges()
    }

    private val roomList = roomListService.createRoomList(
        pageSize = PAGE_SIZE,
        source = RoomList.Source.All,
        coroutineScope = sessionCoroutineScope
    )
    private val _roomSummariesFlow = MutableSharedFlow<ImmutableList<RoomListRoomSummary>>(replay = 1)

    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<RoomListRoomSummary>()
    private val diffCacheUpdater = DiffCacheUpdater<RoomSummary, RoomListRoomSummary>(diffCache = diffCache, detectMoves = true) { old, new ->
        old?.roomId == new?.roomId
    }

    val roomSummariesFlow: Flow<ImmutableList<RoomListRoomSummary>> = _roomSummariesFlow

    val loadingState = roomList.loadingState

    fun launchIn(coroutineScope: CoroutineScope) {
        roomList
            .summaries
            .onEach { roomSummaries ->
                replaceWith(roomSummaries)
            }
            .launchIn(coroutineScope)
    }

    suspend fun updateFilter(filter: RoomListFilter) {
        roomList.updateFilter(filter)
    }

    suspend fun updateVisibleRange(visibleRange: IntRange) = coroutineScope {
        launch {
            roomList.updateVisibleRange(visibleRange, PAGINATION_THRESHOLD)
        }
        launch {
            subscribeToVisibleRoomsIfNeeded(visibleRange)
        }
    }

    private var currentSubscribeToVisibleRoomsJob: Job? = null
    private fun CoroutineScope.subscribeToVisibleRoomsIfNeeded(range: IntRange) {
        currentSubscribeToVisibleRoomsJob?.cancel()
        currentSubscribeToVisibleRoomsJob = launch {
            // Debounce the subscription to avoid subscribing to too many rooms
            delay(SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS)

            if (range.isEmpty()) return@launch
            val currentRoomList = roomSummariesFlow.first()
            // Use extended range to 'prefetch' the next rooms info
            val midExtendedRangeSize = EXTENDED_VISIBILITY_RANGE_SIZE / 2
            val extendedRange = range.first until range.last + midExtendedRangeSize
            val roomIds = extendedRange.mapNotNull { index ->
                currentRoomList.getOrNull(index)?.roomId
            }
            roomListService.subscribeToVisibleRooms(roomIds)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeNotificationSettings() {
        notificationSettingsService.notificationSettingsChangeFlow
            .debounce(0.5.seconds)
            .onEach {
                roomList.rebuildSummaries()
            }
            .launchIn(sessionCoroutineScope)
    }

    private fun observeDateTimeChanges() {
        dateTimeObserver.changes
            .onEach { event ->
                when (event) {
                    is DateTimeObserver.Event.TimeZoneChanged -> rebuildAllRoomSummaries()
                    is DateTimeObserver.Event.DateChanged -> rebuildAllRoomSummaries()
                }
            }
            .launchIn(sessionCoroutineScope)
    }

    private suspend fun replaceWith(roomSummaries: List<RoomSummary>) = withContext(coroutineDispatchers.computation) {
        lock.withLock {
            val filtered = roomSummaries.filter { !isHiddenBridgeRoom(it) }
            diffCacheUpdater.updateWith(filtered)
            buildAndEmitAllRooms(filtered)
        }
    }

    /**
     * Hide bot DMs and bridge ghost rooms from the main room list.
     * These rooms are automatically moved into their respective spaces
     * (WhatsApp / Instagram) during bridge onboarding.
     *
     * Detection is three-dimensional:
     * 1. Hero-based — the other member in a DM is the bridge bot or a ghost user.
     * 2. Alias-based — the room alias contains bridge-specific prefixes.
     * 3. Creator-based — the room was created by a bridge bot.
     */
    private fun isHiddenBridgeRoom(summary: RoomSummary): Boolean {
        val info = summary.info
        val botUserIds = setOf(
            AuthenticationConfig.WHATSAPP_BRIDGE_BOT,
            AuthenticationConfig.META_BRIDGE_BOT,
        )
        val bridgePatterns = listOf(
            Regex("^@whatsapp_.+", RegexOption.IGNORE_CASE),
            Regex("^@meta_.+", RegexOption.IGNORE_CASE),
        )
        val metaAiPattern = Regex("meta\\s*ai|ai\\s*assistant", RegexOption.IGNORE_CASE)

        // Never hide PrismAI / Meta AI rooms — they are special and should appear in the main list
        if (info.name?.let { metaAiPattern.matches(it) } == true) return false
        if (info.heroes.any { it.displayName?.let { dn -> metaAiPattern.matches(dn) } == true }) return false

        val altAliases = info.alternativeAliases.map { it.value }
        val canonicalAlias = info.canonicalAlias?.value

        // 1. Hide bot DMs (1-to-1 with bridge bot)
        if (summary.isOneToOne && info.heroes.any { it.userId.value in botUserIds }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bot DM)", summary.roomId.value)
            return true
        }

        // 2. Hide bridge chat rooms — they live inside their Space (WhatsApp / Instagram)
        if (info.heroes.any { hero -> bridgePatterns.any { it.matches(hero.userId.value) } }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bridge ghost)", summary.roomId.value)
            return true
        }
        if (info.aliases.any { alias -> bridgePatterns.any { it.matches(alias.value) } }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bridge alias)", summary.roomId.value)
            return true
        }
        if (info.creators.any { it.value in botUserIds }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bridge creator)", summary.roomId.value)
            return true
        }
        if (canonicalAlias != null && bridgePatterns.any { it.matches(canonicalAlias) }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bridge canonicalAlias)", summary.roomId.value)
            return true
        }
        if (info.alternativeAliases.any { alias -> bridgePatterns.any { it.matches(alias.value) } }) {
            timber.log.Timber.d("RoomListDataSource: HIDING room=%s (bridge altAlias)", summary.roomId.value)
            return true
        }

        return false
    }

    private suspend fun buildAndEmitAllRooms(roomSummaries: List<RoomSummary>, useCache: Boolean = true) {
        // Used to detect duplicates in the room list summaries - see comment below
        data class CacheResult(val index: Int, val fromCache: Boolean)

        val cachingResults = mutableMapOf<RoomId, MutableList<CacheResult>>()

        val roomListRoomSummaries = diffCache.indices().mapNotNull { index ->
            if (useCache) {
                diffCache.get(index)?.let { cachedItem ->
                    // Add the cached item to the caching results
                    val pairs = cachingResults.getOrDefault(cachedItem.roomId, mutableListOf())
                    pairs.add(CacheResult(index, fromCache = true))
                    cachingResults[cachedItem.roomId] = pairs
                    cachedItem
                } ?: run {
                    roomSummaries.getOrNull(index)?.roomId?.let {
                        // Add the non-cached item to the caching results
                        val pairs = cachingResults.getOrDefault(it, mutableListOf())
                        pairs.add(CacheResult(index, fromCache = false))
                        cachingResults[it] = pairs
                    }
                    buildAndCacheItem(roomSummaries, index)
                }
            } else {
                roomSummaries.getOrNull(index)?.roomId?.let {
                    // Add the non-cached item to the caching results
                    val pairs = cachingResults.getOrDefault(it, mutableListOf())
                    pairs.add(CacheResult(index, fromCache = false))
                    cachingResults[it] = pairs
                }
                buildAndCacheItem(roomSummaries, index)
            }
        }

        // TODO remove once https://github.com/prism-hq/prism-x-android/issues/5031 has been confirmed as fixed
        val duplicates = cachingResults.filter { (_, operations) -> operations.size > 1 }
        if (duplicates.isNotEmpty()) {
            analyticsService.trackError(
                IllegalStateException(
                    "Found duplicates in room summaries after a local UI update: $duplicates. " +
                        "This could be a race condition/caching issue of some kind"
                )
            )

            // Remove duplicates before emitting the new values
            _roomSummariesFlow.emit(roomListRoomSummaries.distinctBy { it.roomId }.toImmutableList())
        } else {
            _roomSummariesFlow.emit(roomListRoomSummaries.toImmutableList())
        }
    }

    private fun buildAndCacheItem(roomSummaries: List<RoomSummary>, index: Int): RoomListRoomSummary? {
        val roomListSummary = roomSummaries.getOrNull(index)?.let { roomListRoomSummaryFactory.create(it) }
        diffCache[index] = roomListSummary
        return roomListSummary
    }

    private suspend fun rebuildAllRoomSummaries() {
        lock.withLock {
            roomList.summaries.replayCache.firstOrNull()?.let { roomSummaries ->
                val filtered = roomSummaries.filter { !isHiddenBridgeRoom(it) }
                buildAndEmitAllRooms(filtered, useCache = false)
            }
        }
    }
}
