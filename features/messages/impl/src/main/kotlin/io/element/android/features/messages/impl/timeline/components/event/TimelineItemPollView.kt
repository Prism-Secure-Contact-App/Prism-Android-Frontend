/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.features.messages.impl.timeline.TimelineEvent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemPollContentProvider
import io.prism.android.features.poll.api.pollcontent.PollContentView
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.prism.api.core.EventId
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TimelineItemPollView(
    content: TimelineItemPollContent,
    eventSink: (TimelineEvent.TimelineItemPollEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onSelectAnswer(pollStartId: EventId, answerId: String) {
        eventSink(TimelineEvent.SelectPollAnswer(pollStartId, answerId))
    }

    fun onEndPoll(pollStartId: EventId) {
        eventSink(TimelineEvent.EndPoll(pollStartId))
    }

    fun onEditPoll(pollStartId: EventId) {
        eventSink(TimelineEvent.EditPoll(pollStartId))
    }

    PollContentView(
        eventId = content.eventId,
        question = content.question,
        answerItems = content.answerItems.toImmutableList(),
        pollKind = content.pollKind,
        isPollEnded = content.isEnded,
        isPollEditable = content.isEditable,
        isMine = content.isMine,
        onSelectAnswer = ::onSelectAnswer,
        onEditPoll = ::onEditPoll,
        onEndPoll = ::onEndPoll,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemPollViewPreview(@PreviewParameter(TimelineItemPollContentProvider::class) content: TimelineItemPollContent) =
    PRISMPreview {
        TimelineItemPollView(
            content = content,
            eventSink = {},
        )
    }
