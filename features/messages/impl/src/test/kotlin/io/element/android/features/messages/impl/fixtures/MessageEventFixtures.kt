/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.fixtures

import io.prism.android.features.messages.impl.timeline.aTimelineItemDebugInfo
import io.prism.android.features.messages.impl.timeline.aTimelineItemReactions
import io.prism.android.features.messages.impl.timeline.model.ReadReceiptData
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.TimelineItemReadReceipts
import io.prism.android.features.messages.impl.timeline.model.TimelineItemThreadInfo
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemTextContent
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.TransactionId
import io.prism.android.libraries.prism.api.core.UniqueId
import io.prism.android.libraries.prism.api.timeline.item.event.LocalEventSendState
import io.prism.android.libraries.prism.api.timeline.item.event.MessageShieldProvider
import io.prism.android.libraries.prism.api.timeline.item.event.SendHandleProvider
import io.prism.android.libraries.prism.api.timeline.item.event.TimelineItemDebugInfoProvider
import io.prism.android.libraries.prism.test.AN_EVENT_ID
import io.prism.android.libraries.prism.test.A_MESSAGE
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_NAME
import io.prism.android.libraries.prism.test.core.FakeSendHandle
import io.prism.android.libraries.prism.ui.messages.reply.InReplyToDetails
import io.prism.android.libraries.prism.ui.messages.reply.aProfileDetailsReady
import kotlinx.collections.immutable.toImmutableList

internal fun aMessageEvent(
    eventId: EventId? = AN_EVENT_ID,
    transactionId: TransactionId? = null,
    isMine: Boolean = true,
    isEditable: Boolean = true,
    canBeRepliedTo: Boolean = true,
    content: TimelineItemEventContent = TimelineItemTextContent(body = A_MESSAGE, htmlDocument = null, formattedBody = A_MESSAGE, isEdited = false),
    inReplyTo: InReplyToDetails? = null,
    threadInfo: TimelineItemThreadInfo? = null,
    sendState: LocalEventSendState = LocalEventSendState.Sent(AN_EVENT_ID),
    debugInfoProvider: TimelineItemDebugInfoProvider = TimelineItemDebugInfoProvider { aTimelineItemDebugInfo() },
    messageShieldProvider: MessageShieldProvider = MessageShieldProvider { null },
    sendHandleProvider: SendHandleProvider = SendHandleProvider { FakeSendHandle() }
) = TimelineItem.Event(
    id = UniqueId(eventId?.value.orEmpty()),
    eventId = eventId,
    transactionId = transactionId,
    senderId = A_USER_ID,
    senderProfile = aProfileDetailsReady(displayName = A_USER_NAME),
    senderAvatar = AvatarData(A_USER_ID.value, A_USER_NAME, size = AvatarSize.TimelineSender),
    content = content,
    sentTime = "",
    isMine = isMine,
    isEditable = isEditable,
    canBeRepliedTo = canBeRepliedTo,
    reactionsState = aTimelineItemReactions(count = 0),
    readReceiptState = TimelineItemReadReceipts(emptyList<ReadReceiptData>().toImmutableList()),
    localSendState = sendState,
    inReplyTo = inReplyTo,
    threadInfo = threadInfo,
    origin = null,
    timelineItemDebugInfoProvider = debugInfoProvider,
    messageShieldProvider = messageShieldProvider,
    sendHandleProvider = sendHandleProvider,
    forwarder = null,
    forwarderProfile = null,
)
