/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.extensions.DEFAULT_SAFE_LENGTH
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.eventformatter.api.RoomLatestEventFormatter
import io.prism.android.libraries.eventformatter.impl.mode.RenderingMode
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.permalink.PermalinkParser
import io.prism.android.libraries.prism.api.roomlist.LatestEventValue
import io.prism.android.libraries.prism.api.timeline.item.event.AudioMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.CallNotifyContent
import io.prism.android.libraries.prism.api.timeline.item.event.EmoteMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.EventContent
import io.prism.android.libraries.prism.api.timeline.item.event.FailedToParseMessageLikeContent
import io.prism.android.libraries.prism.api.timeline.item.event.FailedToParseStateContent
import io.prism.android.libraries.prism.api.timeline.item.event.FileMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.ImageMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.LegacyCallInviteContent
import io.prism.android.libraries.prism.api.timeline.item.event.LiveLocationContent
import io.prism.android.libraries.prism.api.timeline.item.event.LocationMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.MessageContent
import io.prism.android.libraries.prism.api.timeline.item.event.MessageType
import io.prism.android.libraries.prism.api.timeline.item.event.NoticeMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.OtherMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.PollContent
import io.prism.android.libraries.prism.api.timeline.item.event.ProfileChangeContent
import io.prism.android.libraries.prism.api.timeline.item.event.RedactedContent
import io.prism.android.libraries.prism.api.timeline.item.event.RoomMembershipContent
import io.prism.android.libraries.prism.api.timeline.item.event.StateContent
import io.prism.android.libraries.prism.api.timeline.item.event.StickerContent
import io.prism.android.libraries.prism.api.timeline.item.event.StickerMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.TextMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.UnableToDecryptContent
import io.prism.android.libraries.prism.api.timeline.item.event.UnknownContent
import io.prism.android.libraries.prism.api.timeline.item.event.VideoMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.VoiceMessageType
import io.prism.android.libraries.prism.api.timeline.item.event.getDisambiguatedDisplayName
import io.prism.android.libraries.prism.ui.messages.toPlainText
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.services.toolbox.api.strings.StringProvider

@ContributesBinding(SessionScope::class)
class DefaultRoomLatestEventFormatter(
    private val sp: StringProvider,
    private val roomMembershipContentFormatter: RoomMembershipContentFormatter,
    private val profileChangeContentFormatter: ProfileChangeContentFormatter,
    private val stateContentFormatter: StateContentFormatter,
    private val permalinkParser: PermalinkParser,
) : RoomLatestEventFormatter {
    override fun format(
        latestEvent: LatestEventValue.Local,
        isDmRoom: Boolean,
    ): CharSequence? = formatContent(
        content = latestEvent.content,
        isDmRoom = isDmRoom,
        isOutgoing = true,
        senderId = latestEvent.senderId,
        senderDisambiguatedDisplayName = latestEvent.senderProfile.getDisambiguatedDisplayName(latestEvent.senderId)
    )

    override fun format(
        latestEvent: LatestEventValue.Remote,
        isDmRoom: Boolean,
    ): CharSequence? = formatContent(
        content = latestEvent.content,
        isDmRoom = isDmRoom,
        isOutgoing = latestEvent.isOwn,
        senderId = latestEvent.senderId,
        senderDisambiguatedDisplayName = latestEvent.senderProfile.getDisambiguatedDisplayName(latestEvent.senderId)
    )

    private fun formatContent(
        content: EventContent,
        isDmRoom: Boolean,
        isOutgoing: Boolean,
        senderId: UserId,
        senderDisambiguatedDisplayName: String
    ): CharSequence? {
        return when (content) {
            is MessageContent -> content.process(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            RedactedContent -> {
                val message = sp.getString(CommonStrings.common_message_removed)
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is StickerContent -> {
                val message = sp.getString(CommonStrings.common_sticker) + " (" + content.bestDescription + ")"
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is UnableToDecryptContent -> {
                val message = sp.getString(CommonStrings.common_waiting_for_decryption_key)
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is RoomMembershipContent -> {
                roomMembershipContentFormatter.format(content, senderDisambiguatedDisplayName, isOutgoing)
            }
            is ProfileChangeContent -> {
                profileChangeContentFormatter.format(content, senderId, senderDisambiguatedDisplayName, isOutgoing)
            }
            is StateContent -> {
                stateContentFormatter.format(content, senderDisambiguatedDisplayName, isOutgoing, RenderingMode.RoomList)
            }
            is PollContent -> {
                val message = sp.getString(CommonStrings.common_poll_summary, content.question)
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is FailedToParseMessageLikeContent, is FailedToParseStateContent, is UnknownContent -> {
                val message = sp.getString(CommonStrings.common_unsupported_event)
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is LiveLocationContent -> {
                val message = sp.getString(CommonStrings.common_shared_location)
                message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
            }
            is LegacyCallInviteContent -> sp.getString(CommonStrings.common_unsupported_call)
            is CallNotifyContent -> sp.getString(CommonStrings.common_call_started)
        }?.take(DEFAULT_SAFE_LENGTH)
    }

    private fun MessageContent.process(
        senderDisambiguatedDisplayName: String,
        isDmRoom: Boolean,
        isOutgoing: Boolean
    ): CharSequence {
        val message = when (val messageType: MessageType = type) {
            // Doesn't need a prefix
            is EmoteMessageType -> {
                return "* $senderDisambiguatedDisplayName ${messageType.body}"
            }
            is TextMessageType -> {
                messageType.toPlainText(permalinkParser)
            }
            is VideoMessageType -> {
                messageType.bestDescription.prefixWith(sp.getString(CommonStrings.common_video))
            }
            is ImageMessageType -> {
                messageType.bestDescription.prefixWith(sp.getString(CommonStrings.common_image))
            }
            is StickerMessageType -> {
                messageType.bestDescription.prefixWith(sp.getString(CommonStrings.common_sticker))
            }
            is LocationMessageType -> {
                sp.getString(CommonStrings.common_shared_location)
            }
            is FileMessageType -> {
                messageType.bestDescription.prefixWith(sp.getString(CommonStrings.common_file))
            }
            is AudioMessageType -> {
                messageType.bestDescription.prefixWith(sp.getString(CommonStrings.common_audio))
            }
            is VoiceMessageType -> {
                // In this case, do not use bestDescription, because the filename is useless, only use the caption if available.
                messageType.caption?.prefixWith(sp.getString(CommonStrings.common_voice_message))
                    ?: sp.getString(CommonStrings.common_voice_message)
            }
            is OtherMessageType -> {
                messageType.body
            }
            is NoticeMessageType -> {
                messageType.body
            }
        }
        return message.prefixIfNeeded(senderDisambiguatedDisplayName, isDmRoom, isOutgoing)
    }

    private fun CharSequence.prefixIfNeeded(
        senderDisambiguatedDisplayName: String,
        isDmRoom: Boolean,
        isOutgoing: Boolean,
    ): CharSequence = if (isDmRoom) {
        this
    } else {
        prefixWith(
            if (isOutgoing) {
                sp.getString(CommonStrings.common_you)
            } else {
                senderDisambiguatedDisplayName
            }
        )
    }
}
