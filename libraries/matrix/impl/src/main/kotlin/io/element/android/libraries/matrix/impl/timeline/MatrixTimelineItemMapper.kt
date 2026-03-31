/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.timeline

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UniqueId
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.impl.timeline.item.event.EventTimelineItemMapper
import io.prism.android.libraries.prism.impl.timeline.item.virtual.VirtualTimelineItemMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.prism.rustcomponents.sdk.TimelineItem

class PRISMTimelineItemMapper(
    private val fetchDetailsForEvent: suspend (EventId) -> Result<Unit>,
    private val coroutineScope: CoroutineScope,
    private val virtualTimelineItemMapper: VirtualTimelineItemMapper,
    private val eventTimelineItemMapper: EventTimelineItemMapper,
) {
    fun map(timelineItem: TimelineItem): PRISMTimelineItem = timelineItem.use {
        val uniqueId = UniqueId(timelineItem.uniqueId().id)
        val asEvent = it.asEvent()
        if (asEvent != null) {
            val eventTimelineItem = eventTimelineItemMapper.map(asEvent)
            if (eventTimelineItem.hasNotLoadedInReplyTo() && eventTimelineItem.eventId != null) {
                fetchEventDetails(eventTimelineItem.eventId!!)
            }

            return PRISMTimelineItem.Event(uniqueId, eventTimelineItem)
        }
        val asVirtual = it.asVirtual()
        if (asVirtual != null) {
            val virtualTimelineItem = virtualTimelineItemMapper.map(asVirtual)
            return PRISMTimelineItem.Virtual(uniqueId, virtualTimelineItem)
        }
        return PRISMTimelineItem.Other
    }

    private fun fetchEventDetails(eventId: EventId) = coroutineScope.launch {
        fetchDetailsForEvent(eventId)
    }
}
