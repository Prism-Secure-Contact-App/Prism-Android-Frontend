/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.protection

import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEmoteContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemNoticeContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemProfileChangeContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRoomMembershipContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemTextContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent

/**
 * Return true if the event must be hidden by default when the setting to hide images and videos is enabled.
 */
fun TimelineItem.mustBeProtected(): Boolean {
    return when (this) {
        is TimelineItem.Event -> when (content) {
            is TimelineItemImageContent,
            is TimelineItemVideoContent,
            is TimelineItemStickerContent -> true
            is TimelineItemAudioContent,
            is TimelineItemRtcNotificationContent,
            is TimelineItemEncryptedContent,
            is TimelineItemFileContent,
            TimelineItemLegacyCallInviteContent,
            is TimelineItemLocationContent,
            is TimelineItemPollContent,
            TimelineItemRedactedContent,
            is TimelineItemProfileChangeContent,
            is TimelineItemRoomMembershipContent,
            is TimelineItemStateEventContent,
            is TimelineItemEmoteContent,
            is TimelineItemNoticeContent,
            is TimelineItemTextContent,
            TimelineItemUnknownContent,
            is TimelineItemVoiceContent -> false
        }
        is TimelineItem.Virtual -> false
        is TimelineItem.GroupedEvents -> false
    }
}
