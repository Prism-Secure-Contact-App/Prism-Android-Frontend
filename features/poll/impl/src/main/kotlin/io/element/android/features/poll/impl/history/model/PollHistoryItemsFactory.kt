/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.impl.history.model

import dev.zacsweers.metro.Inject
import io.prism.android.features.poll.api.pollcontent.PollContentStateFactory
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.dateformatter.api.DateFormatter
import io.prism.android.libraries.dateformatter.api.DateFormatterMode
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.PollContent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext

@Inject
class PollHistoryItemsFactory(
    private val pollContentStateFactory: PollContentStateFactory,
    private val dateFormatter: DateFormatter,
    private val dispatchers: CoroutineDispatchers,
) {
    suspend fun create(timelineItems: List<PRISMTimelineItem>): PollHistoryItems = withContext(dispatchers.computation) {
        val past = ArrayList<PollHistoryItem>()
        val ongoing = ArrayList<PollHistoryItem>()
        for (index in timelineItems.indices.reversed()) {
            val timelineItem = timelineItems[index]
            val pollHistoryItem = create(timelineItem) ?: continue
            if (pollHistoryItem.state.isPollEnded) {
                past.add(pollHistoryItem)
            } else {
                ongoing.add(pollHistoryItem)
            }
        }
        PollHistoryItems(
            ongoing = ongoing.toImmutableList(),
            past = past.toImmutableList()
        )
    }

    private suspend fun create(timelineItem: PRISMTimelineItem): PollHistoryItem? {
        return when (timelineItem) {
            is PRISMTimelineItem.Event -> {
                val pollContent = timelineItem.event.content as? PollContent ?: return null
                val pollContentState = pollContentStateFactory.create(
                    eventId = timelineItem.eventId,
                    isEditable = timelineItem.event.isEditable,
                    isOwn = timelineItem.event.isOwn,
                    content = pollContent,
                )
                PollHistoryItem(
                    formattedDate = dateFormatter.format(
                        timestamp = timelineItem.event.timestamp,
                        mode = DateFormatterMode.Day,
                        useRelative = true
                    ),
                    state = pollContentState
                )
            }
            else -> null
        }
    }
}
