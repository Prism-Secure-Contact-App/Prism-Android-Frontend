/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.factories

import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.features.messages.impl.timeline.diff.TimelineItemsCacheInvalidator
import io.prism.android.features.messages.impl.timeline.factories.event.TimelineItemEventFactory
import io.prism.android.features.messages.impl.timeline.factories.virtual.TimelineItemVirtualFactory
import io.prism.android.features.messages.impl.timeline.groups.TimelineItemGrouper
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.libraries.androidutils.diff.DiffCacheUpdater
import io.prism.android.libraries.androidutils.diff.MutableListDiffCache
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.matrix.api.room.RoomMember
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@AssistedInject
class TimelineItemsFactory(
    @Assisted config: TimelineItemsFactoryConfig,
    eventItemFactoryCreator: TimelineItemEventFactory.Creator,
    private val dispatchers: CoroutineDispatchers,
    private val virtualItemFactory: TimelineItemVirtualFactory,
    private val timelineItemGrouper: TimelineItemGrouper,
) {
    @AssistedFactory
    interface Creator {
        fun create(config: TimelineItemsFactoryConfig): TimelineItemsFactory
    }

    private val eventItemFactory = eventItemFactoryCreator.create(config)
    private val _timelineItems = MutableSharedFlow<ImmutableList<TimelineItem>>(replay = 1)
    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<TimelineItem>()
    private val diffCacheUpdater = DiffCacheUpdater<PRISMTimelineItem, TimelineItem>(
        diffCache = diffCache,
        detectMoves = false,
        cacheInvalidator = TimelineItemsCacheInvalidator()
    ) { old, new ->
        if (old is PRISMTimelineItem.Event && new is PRISMTimelineItem.Event) {
            old.uniqueId == new.uniqueId
        } else {
            false
        }
    }

    val timelineItems: Flow<ImmutableList<TimelineItem>> = _timelineItems.distinctUntilChanged()

    suspend fun replaceWith(
        timelineItems: List<PRISMTimelineItem>,
        roomMembers: List<RoomMember>,
    ) = withContext(dispatchers.computation) {
        lock.withLock {
            diffCacheUpdater.updateWith(timelineItems)
            buildAndEmitTimelineItemStates(timelineItems, roomMembers)
        }
    }

    private suspend fun buildAndEmitTimelineItemStates(
        timelineItems: List<PRISMTimelineItem>,
        roomMembers: List<RoomMember>,
    ) {
        val newTimelineItemStates = ArrayList<TimelineItem>()
        for (index in diffCache.indices().reversed()) {
            val cacheItem = diffCache.get(index)
            if (cacheItem == null) {
                buildAndCacheItem(timelineItems, index, roomMembers)?.also { timelineItemState ->
                    newTimelineItemStates.add(timelineItemState)
                }
            } else {
                val updatedItem = if (cacheItem is TimelineItem.Event && roomMembers.isNotEmpty()) {
                    eventItemFactory.update(
                        timelineItem = cacheItem,
                        receivedPRISMTimelineItem = timelineItems[index] as PRISMTimelineItem.Event,
                        roomMembers = roomMembers
                    )
                } else {
                    cacheItem
                }
                newTimelineItemStates.add(updatedItem)
            }
        }
        val result = timelineItemGrouper.group(newTimelineItemStates).toImmutableList()
        this._timelineItems.emit(result)
    }

    private suspend fun buildAndCacheItem(
        timelineItems: List<PRISMTimelineItem>,
        index: Int,
        roomMembers: List<RoomMember>,
    ): TimelineItem? {
        val timelineItem =
            when (val currentTimelineItem = timelineItems[index]) {
                is PRISMTimelineItem.Event -> eventItemFactory.create(currentTimelineItem, index, timelineItems, roomMembers)
                is PRISMTimelineItem.Virtual -> virtualItemFactory.create(currentTimelineItem)
                PRISMTimelineItem.Other -> null
            }
        diffCache[index] = timelineItem
        return timelineItem
    }
}
