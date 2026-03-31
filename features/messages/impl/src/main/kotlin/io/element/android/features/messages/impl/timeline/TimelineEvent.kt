/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline

import io.prism.android.features.messages.impl.timeline.components.MessageShieldData
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.timeline.Timeline
import kotlin.time.Duration

sealed interface TimelineEvent {
    data class OnScrollFinished(val firstIndex: Int) : TimelineEvent
    data class FocusOnEvent(val eventId: EventId, val debounce: Duration = Duration.ZERO) : TimelineEvent
    data object ClearFocusRequestState : TimelineEvent
    data object OnFocusEventRender : TimelineEvent
    data object JumpToLive : TimelineEvent

    data object HideShieldDialog : TimelineEvent

    /**
     * Events coming from a timeline item.
     */
    sealed interface TimelineItemEvent : TimelineEvent

    data class ComputeVerifiedUserSendFailure(val event: TimelineItem.Event) : TimelineItemEvent
    data class ShowShieldDialog(val messageShieldData: MessageShieldData) : TimelineItemEvent
    data class LoadMore(val direction: Timeline.PaginationDirection) : TimelineItemEvent
    data class OpenThread(val threadRootEventId: ThreadId, val focusedEvent: EventId?) : TimelineItemEvent

    /**
     * Navigate to the predecessor or successor room of the current room.
     */
    data class NavigateToPredecessorOrSuccessorRoom(val roomId: RoomId) : TimelineItemEvent

    /**
     * Events coming from a poll item.
     */
    sealed interface TimelineItemPollEvent : TimelineItemEvent

    data class SelectPollAnswer(
        val pollStartId: EventId,
        val answerId: String
    ) : TimelineItemPollEvent

    data class EndPoll(
        val pollStartId: EventId,
    ) : TimelineItemPollEvent

    data class EditPoll(
        val pollStartId: EventId,
    ) : TimelineItemPollEvent
}
