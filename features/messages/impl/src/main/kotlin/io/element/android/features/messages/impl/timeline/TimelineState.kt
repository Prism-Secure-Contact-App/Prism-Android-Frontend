/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline

import androidx.compose.runtime.Immutable
import io.prism.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import io.prism.android.features.messages.impl.timeline.components.MessageShieldData
import io.prism.android.features.messages.impl.timeline.model.NewEventState
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.typing.TypingNotificationState
import io.prism.android.features.roomcall.api.RoomCallState
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UniqueId
import io.prism.android.libraries.prism.api.room.tombstone.PredecessorRoom
import io.prism.android.libraries.prism.api.timeline.Timeline
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration

data class TimelineState(
    val timelineItems: ImmutableList<TimelineItem>,
    val timelineRoomInfo: TimelineRoomInfo,
    val timelineMode: Timeline.Mode,
    val renderReadReceipts: Boolean,
    val newEventState: NewEventState,
    val isLive: Boolean,
    val focusRequestState: FocusRequestState,
    // If not null, info will be rendered in a dialog
    val messageShieldDialogData: MessageShieldData?,
    val resolveVerifiedUserSendFailureState: ResolveVerifiedUserSendFailureState,
    val displayThreadSummaries: Boolean,
    val eventSink: (TimelineEvent) -> Unit,
) {
    private val lastTimelineEvent = timelineItems.firstOrNull { it is TimelineItem.Event } as? TimelineItem.Event
    val hasAnyEvent = lastTimelineEvent != null
    val focusedEventId = focusRequestState.eventId()

    fun isLastOutgoingMessage(uniqueId: UniqueId): Boolean {
        return isLive && lastTimelineEvent != null && lastTimelineEvent.isMine && lastTimelineEvent.id == uniqueId
    }
}

@Immutable
sealed interface FocusRequestState {
    data object None : FocusRequestState
    data class Requested(val eventId: EventId, val debounce: Duration) : FocusRequestState
    data class Loading(val eventId: EventId) : FocusRequestState
    data class Success(
        val eventId: EventId,
        val index: Int = -1,
        // This is used to know if the event has been rendered yet.
        val rendered: Boolean = false,
    ) : FocusRequestState {
        val isIndexed
            get() = index != -1
    }

    data class Failure(val throwable: Throwable) : FocusRequestState

    fun eventId(): EventId? {
        return when (this) {
            is Requested -> eventId
            is Loading -> eventId
            is Success -> eventId
            else -> null
        }
    }
}

data class TimelineRoomInfo(
    val isDm: Boolean,
    val name: String?,
    val userHasPermissionToSendMessage: Boolean,
    val userHasPermissionToSendReaction: Boolean,
    val roomCallState: RoomCallState,
    val pinnedEventIds: ImmutableList<EventId>,
    val typingNotificationState: TypingNotificationState,
    val predecessorRoom: PredecessorRoom?,
)
