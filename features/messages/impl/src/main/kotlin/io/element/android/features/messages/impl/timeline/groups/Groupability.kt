/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.groups

import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemProfileChangeContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRoomMembershipContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.api.timeline.item.event.CallNotifyContent
import io.prism.android.libraries.prism.api.timeline.item.event.FailedToParseMessageLikeContent
import io.prism.android.libraries.prism.api.timeline.item.event.FailedToParseStateContent
import io.prism.android.libraries.prism.api.timeline.item.event.LegacyCallInviteContent
import io.prism.android.libraries.prism.api.timeline.item.event.LiveLocationContent
import io.prism.android.libraries.prism.api.timeline.item.event.MessageContent
import io.prism.android.libraries.prism.api.timeline.item.event.PollContent
import io.prism.android.libraries.prism.api.timeline.item.event.ProfileChangeContent
import io.prism.android.libraries.prism.api.timeline.item.event.RedactedContent
import io.prism.android.libraries.prism.api.timeline.item.event.RoomMembershipContent
import io.prism.android.libraries.prism.api.timeline.item.event.StateContent
import io.prism.android.libraries.prism.api.timeline.item.event.StickerContent
import io.prism.android.libraries.prism.api.timeline.item.event.UnableToDecryptContent
import io.prism.android.libraries.prism.api.timeline.item.event.UnknownContent

/**
 * Return true if the Event can be grouped in a collapse/expand block
 * When [canBeGrouped] returns a value, [canBeDisplayedInBubbleBlock] MUST return the opposite value.
 * Since the receiving type are not the same, the two functions exist.
 */
internal fun TimelineItem.Event.canBeGrouped(): Boolean {
    return when (content) {
        is TimelineItemTextBasedContent,
        is TimelineItemEncryptedContent,
        is TimelineItemImageContent,
        is TimelineItemStickerContent,
        is TimelineItemFileContent,
        is TimelineItemVideoContent,
        is TimelineItemAudioContent,
        is TimelineItemLocationContent,
        is TimelineItemPollContent,
        is TimelineItemVoiceContent,
        TimelineItemRedactedContent,
        TimelineItemUnknownContent,
        is TimelineItemLegacyCallInviteContent,
        is TimelineItemRtcNotificationContent -> false
        is TimelineItemProfileChangeContent,
        is TimelineItemRoomMembershipContent,
        is TimelineItemStateEventContent -> true
    }
}

/**
 * Return true if the Event can be grouped in a block of message bubbles.
 * When [canBeDisplayedInBubbleBlock] returns a value, [canBeGrouped] MUST return the opposite value.
 * Since the receiving type are not the same, the two functions exist.
 */
internal fun PRISMTimelineItem.Event.canBeDisplayedInBubbleBlock(): Boolean {
    return when (event.content) {
        // Can be grouped
        is FailedToParseMessageLikeContent,
        is MessageContent,
        RedactedContent,
        is StickerContent,
        is PollContent,
        is UnableToDecryptContent,
        is LiveLocationContent -> true
        // Can't be grouped
        is FailedToParseStateContent,
        is ProfileChangeContent,
        is RoomMembershipContent,
        UnknownContent,
        is LegacyCallInviteContent,
        CallNotifyContent,
        is StateContent -> false
    }
}
