/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import io.prism.android.features.messages.impl.attachments.Attachment
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.ThreadId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import kotlinx.collections.immutable.ImmutableList

interface MessagesNavigator {
    fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)
    fun forwardEvent(eventId: EventId)
    fun navigateToReportMessage(eventId: EventId, senderId: UserId)
    fun navigateToEditPoll(eventId: EventId)
    fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?)
    fun navigateToRoom(roomId: RoomId, eventId: EventId?, serverNames: List<String>)
    fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?)
    fun close()
}
