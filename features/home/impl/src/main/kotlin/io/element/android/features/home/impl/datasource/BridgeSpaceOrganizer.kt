/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.datasource

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.createroom.CreateRoomParameters
import io.prism.android.libraries.matrix.api.createroom.RoomPreset
import io.prism.android.libraries.matrix.api.roomdirectory.RoomVisibility
import io.prism.android.libraries.matrix.api.roomlist.RoomListService
import io.prism.android.libraries.matrix.api.room.CurrentUserMembership
import io.prism.android.libraries.matrix.api.roomlist.RoomSummary
import io.prism.android.libraries.matrix.api.spaces.SpaceService
import io.prism.android.libraries.preferences.api.store.SessionPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * Automatically organizes bridge rooms (WhatsApp / Instagram) under their
 * respective Spaces as they are created by the mautrix bridges.
 *
 * This runs in the background for the lifetime of the session. It observes
 * all rooms and, when a new bridge room is detected, finds (or creates) the
 * target Space and adds the room as a child.
 */
@Inject
@SingleIn(SessionScope::class)
class BridgeSpaceOrganizer(
    private val roomListService: RoomListService,
    private val spaceService: SpaceService,
    private val matrixClient: PRISMClient,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val sessionPreferencesStore: SessionPreferencesStore,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) {
    private val processedRoomIds = ConcurrentHashMap.newKeySet<RoomId>()
    private val creatingSpaces = ConcurrentHashMap.newKeySet<String>()

    fun start() {
        sessionCoroutineScope.launch(coroutineDispatchers.io) {
            roomListService.allRooms.summaries.collect { summaries ->
                val previousIds = processedRoomIds.toSet()
                val currentIds = summaries.map { it.roomId }.toSet()

                // Auto-accept invites only from configured bridge bots.
                val bridgeBotIds = setOf(
                    AuthenticationConfig.WHATSAPP_BRIDGE_BOT,
                    AuthenticationConfig.META_BRIDGE_BOT,
                )
                for (room in summaries) {
                    if (room.info.currentUserMembership == CurrentUserMembership.INVITED) {
                        val inviterId = room.info.inviter?.userId?.value
                        if (inviterId !in bridgeBotIds) {
                            Timber.d("BridgeSpaceOrganizer: skipping invite to %s from non-bridge inviter %s", room.roomId.value, inviterId)
                            continue
                        }
                        Timber.d("BridgeSpaceOrganizer: auto-accepting invite to %s from %s", room.roomId.value, inviterId)
                        try {
                            matrixClient.joinRoom(room.roomId)
                        } catch (t: Throwable) {
                            Timber.w(t, "BridgeSpaceOrganizer: failed to auto-accept invite to %s", room.roomId.value)
                        }
                    }
                }

                // Find truly new rooms (rooms we haven't seen before)
                val newRooms = summaries.filter { it.roomId !in previousIds }

                for (room in newRooms) {
                    // 1. Check for Meta AI / PrismAI first (highest priority)
                    val isPrismAI = detectPrismAI(room)
                    if (isPrismAI) {
                        Timber.d("BridgeSpaceOrganizer: detected PrismAI room=%s", room.roomId.value)
                        if (organizeRoom(room.roomId, "prism-ai")) {
                            processedRoomIds.add(room.roomId)
                        }
                        continue
                    }

                    // 2. Regular bridge platform detection
                    val platform = detectBridgePlatform(room)
                    Timber.d(
                        "BridgeSpaceOrganizer: new room=%s platform=%s",
                        room.roomId.value,
                        platform,
                    )
                    if (platform != null && organizeRoom(room.roomId, platform)) {
                        processedRoomIds.add(room.roomId)
                    }
                }

                // Also track rooms that have been evaluated but didn't belong to a bridge,
                // so we don't keep re-evaluating them on every sync.
                val nonBridgeNewRooms = newRooms.filter { detectBridgePlatform(it) == null && !detectPrismAI(it) }
                processedRoomIds.addAll(nonBridgeNewRooms.map { it.roomId })

                // Re-evaluate any rooms that were previously non-bridge but might have changed
                // (not expected in normal operation, but keeps the set in sync).
                processedRoomIds.retainAll(currentIds)
            }
        }
    }

    /**
     * Detect whether a room is the Meta AI contact on WhatsApp.
     * Checks room name and hero display name for "Meta AI" (case-insensitive, locale-aware).
     */
    internal fun detectPrismAI(summary: RoomSummary): Boolean {
        val info = summary.info
        val metaAiPattern = Regex("meta\\s*ai|ai\\s*assistant", RegexOption.IGNORE_CASE)

        // Check room name
        info.name?.let { if (metaAiPattern.matches(it)) return true }

        // Check hero display names
        info.heroes.forEach { hero ->
            hero.displayName?.let { if (metaAiPattern.matches(it)) return true }
        }

        return false
    }

    /**
     * Detect whether a room belongs to a bridge platform.
     * @return "whatsapp", "instagram", or null.
     */
    internal fun detectBridgePlatform(summary: RoomSummary): String? {
        val info = summary.info
        val botUserIds = setOf(
            AuthenticationConfig.WHATSAPP_BRIDGE_BOT,
            AuthenticationConfig.META_BRIDGE_BOT,
        )
        val whatsappPattern = Regex("^@whatsapp_.+", RegexOption.IGNORE_CASE)
        val metaPattern = Regex("^@meta_.+", RegexOption.IGNORE_CASE)

        // Skip bot DMs — they should not be added to spaces
        if (summary.isOneToOne && info.heroes.any { it.userId.value in botUserIds }) return null

        // 1. Hero-based detection
        info.heroes.forEach { hero ->
            when {
                whatsappPattern.matches(hero.userId.value) -> return "whatsapp"
                metaPattern.matches(hero.userId.value) -> return "instagram"
            }
        }

        // 2. Alias-based detection
        info.aliases.forEach { alias ->
            when {
                whatsappPattern.matches(alias.value) -> return "whatsapp"
                metaPattern.matches(alias.value) -> return "instagram"
            }
        }

        return null
    }

    private suspend fun organizeRoom(roomId: RoomId, platform: String): Boolean {
        val spaceName = when (platform) {
            "whatsapp" -> "WhatsApp"
            "instagram" -> "Instagram"
            "prism-ai" -> "PrismAI"
            else -> return false
        }

        return try {
            // Find existing space by name
            val spaces = spaceService.topLevelSpacesFlow.first()
            val existingSpace = spaces.find { it.displayName == spaceName }

            val spaceId = if (existingSpace != null) {
                existingSpace.roomId
            } else if (creatingSpaces.add(platform)) {
                // We are the first coroutine to create this space.
                try {
                    val params = CreateRoomParameters(
                        name = spaceName,
                        isEncrypted = true,
                        isDirect = false,
                        visibility = RoomVisibility.Private,
                        preset = RoomPreset.PRIVATE_CHAT,
                        isSpace = true,
                    )
                    matrixClient.createRoom(params).getOrThrow()
                } finally {
                    creatingSpaces.remove(platform)
                }
            } else {
                // Another coroutine is already creating this space.
                // Skip for now; the room will stay unprocessed and be
                // retried on the next sync cycle.
                return false
            }

            spaceService.addChildToSpace(spaceId, roomId).getOrThrow()
            Timber.d("BridgeSpaceOrganizer: added %s to %s space %s", roomId.value, platform, spaceId.value)
            if (platform == "prism-ai") {
                sessionPreferencesStore.setPrismAISpaceId(spaceId.value)
            }
            true
        } catch (t: Throwable) {
            Timber.w(t, "BridgeSpaceOrganizer: failed to add %s to %s space", roomId.value, platform)
            false
        }
    }
}
