/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model.event

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.poll.api.pollcontent.PollAnswerItem
import io.prism.android.features.poll.api.pollcontent.aPollAnswerItemList
import io.prism.android.features.poll.api.pollcontent.aPollQuestion
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.poll.PollKind

open class TimelineItemPollContentProvider : PreviewParameterProvider<TimelineItemPollContent> {
    override val values: Sequence<TimelineItemPollContent>
        get() = sequenceOf(
            aTimelineItemPollContent(),
            aTimelineItemPollContent().copy(pollKind = PollKind.Undisclosed),
            aTimelineItemPollContent().copy(isMine = true),
            aTimelineItemPollContent().copy(isMine = true, isEditable = true),
        )
}

fun aTimelineItemPollContent(
    question: String = aPollQuestion(),
    answerItems: List<PollAnswerItem> = aPollAnswerItemList(),
    isMine: Boolean = false,
    isEditable: Boolean = false,
    isEnded: Boolean = false,
    isEdited: Boolean = false,
): TimelineItemPollContent {
    return TimelineItemPollContent(
        eventId = EventId("\$anEventId"),
        pollKind = PollKind.Disclosed,
        question = question,
        answerItems = answerItems,
        isMine = isMine,
        isEditable = isEditable,
        isEnded = isEnded,
        isEdited = isEdited,
    )
}
