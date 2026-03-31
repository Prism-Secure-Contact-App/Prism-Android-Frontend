/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.impl.data

import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.libraries.core.extensions.flatMap
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.poll.PollKind
import io.prism.android.libraries.prism.api.room.CreateTimelineParams
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.api.timeline.Timeline
import io.prism.android.libraries.prism.api.timeline.TimelineProvider
import io.prism.android.libraries.prism.api.timeline.getActiveTimeline
import io.prism.android.libraries.prism.api.timeline.item.event.PollContent
import io.prism.android.libraries.prism.api.timeline.item.event.toEventOrTransactionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

@AssistedInject
class PollRepository(
    private val room: JoinedRoom,
    private val defaultTimelineProvider: TimelineProvider,
    @Assisted private val timelineMode: Timeline.Mode,
) {
    @AssistedFactory
    fun interface Factory {
        fun create(
            timelineMode: Timeline.Mode,
        ): PollRepository
    }

    suspend fun getPoll(eventId: EventId): Result<PollContent> = runCatchingExceptions {
        getTimelineProvider()
            .getOrThrow()
            .getActiveTimeline()
            .timelineItems
            .first()
            .asSequence()
            .filterIsInstance<PRISMTimelineItem.Event>()
            .first { it.eventId == eventId }
            .event
            .content as PollContent
    }

    suspend fun savePoll(
        existingPollId: EventId?,
        question: String,
        answers: List<String>,
        pollKind: PollKind,
        maxSelections: Int,
    ): Result<Unit> = when (existingPollId) {
        null -> getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider
                .getActiveTimeline()
                .createPoll(
                    question = question,
                    answers = answers,
                    maxSelections = maxSelections,
                    pollKind = pollKind,
                )
        }
        else -> getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider.getActiveTimeline()
                .editPoll(
                    pollStartId = existingPollId,
                    question = question,
                    answers = answers,
                    maxSelections = maxSelections,
                    pollKind = pollKind,
                )
        }
    }

    suspend fun deletePoll(
        pollStartId: EventId,
    ): Result<Unit> =
        getTimelineProvider().flatMap { timelineProvider ->
            timelineProvider.getActiveTimeline()
                .redactEvent(
                    eventOrTransactionId = pollStartId.toEventOrTransactionId(),
                    reason = null,
                )
        }

    private suspend fun getTimelineProvider(): Result<TimelineProvider> {
        return when (timelineMode) {
            is Timeline.Mode.Thread -> {
                val threadedTimelineResult = room.createTimeline(CreateTimelineParams.Threaded(timelineMode.threadRootId))
                threadedTimelineResult.map { threadedTimeline ->
                    object : TimelineProvider {
                        private val flow = MutableStateFlow<Timeline?>(threadedTimeline)
                        override fun activeTimelineFlow(): StateFlow<Timeline?> = flow
                    }
                }
            }
            else -> Result.success(defaultTimelineProvider)
        }
    }
}
