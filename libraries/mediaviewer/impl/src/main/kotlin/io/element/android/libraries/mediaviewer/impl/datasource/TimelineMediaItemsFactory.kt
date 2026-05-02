/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.datasource

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.androidutils.diff.DefaultDiffCacheInvalidator
import io.prism.android.libraries.androidutils.diff.DiffCacheUpdater
import io.prism.android.libraries.androidutils.diff.MutableListDiffCache
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.mediaviewer.impl.model.MediaItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
class TimelineMediaItemsFactory(
    private val dispatchers: CoroutineDispatchers,
    private val virtualItemFactory: VirtualItemFactory,
    private val eventItemFactory: EventItemFactory,
) {
    private val _timelineItems = MutableSharedFlow<ImmutableList<MediaItem>>(replay = 1)
    private val lock = Mutex()
    private val diffCache = MutableListDiffCache<MediaItem>()
    private val diffCacheUpdater = DiffCacheUpdater<PRISMTimelineItem, MediaItem>(
        diffCache = diffCache,
        detectMoves = false,
        cacheInvalidator = DefaultDiffCacheInvalidator()
    ) { old, new ->
        if (old is PRISMTimelineItem.Event && new is PRISMTimelineItem.Event) {
            old.uniqueId == new.uniqueId
        } else {
            false
        }
    }

    val timelineItems: Flow<ImmutableList<MediaItem>> = _timelineItems.distinctUntilChanged()

    suspend fun replaceWith(
        timelineItems: List<PRISMTimelineItem>,
    ) = withContext(dispatchers.computation) {
        lock.withLock {
            diffCacheUpdater.updateWith(timelineItems)
            buildAndEmitTimelineItemStates(timelineItems)
        }
    }

    private suspend fun buildAndEmitTimelineItemStates(
        timelineItems: List<PRISMTimelineItem>,
    ) {
        val newTimelineItemStates = ArrayList<MediaItem>()
        for (index in diffCache.indices().reversed()) {
            val cacheItem = diffCache.get(index)
            if (cacheItem == null) {
                buildAndCacheItem(timelineItems, index)?.also { timelineItemState ->
                    newTimelineItemStates.add(timelineItemState)
                }
            } else {
                newTimelineItemStates.add(cacheItem)
            }
        }
        _timelineItems.emit(newTimelineItemStates.toImmutableList())
    }

    private fun buildAndCacheItem(
        timelineItems: List<PRISMTimelineItem>,
        index: Int,
    ): MediaItem? {
        val timelineItem =
            when (val currentTimelineItem = timelineItems[index]) {
                is PRISMTimelineItem.Event -> eventItemFactory.create(currentTimelineItem)
                is PRISMTimelineItem.Virtual -> virtualItemFactory.create(currentTimelineItem)
                PRISMTimelineItem.Other -> null
            }
        diffCache[index] = timelineItem
        return timelineItem
    }
}
