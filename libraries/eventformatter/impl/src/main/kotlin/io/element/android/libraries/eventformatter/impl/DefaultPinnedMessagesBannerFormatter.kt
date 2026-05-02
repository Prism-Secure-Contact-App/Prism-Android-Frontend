/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.impl

import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.eventformatter.api.PinnedMessagesBannerFormatter
import io.prism.android.libraries.matrix.api.permalink.PermalinkParser
import io.prism.android.libraries.matrix.api.timeline.item.event.AudioMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.EmoteMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.FileMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.ImageMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.LocationMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageContent
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.NoticeMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.OtherMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.PollContent
import io.prism.android.libraries.matrix.api.timeline.item.event.RedactedContent
import io.prism.android.libraries.matrix.api.timeline.item.event.StickerContent
import io.prism.android.libraries.matrix.api.timeline.item.event.StickerMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.TextMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import io.prism.android.libraries.matrix.api.timeline.item.event.VideoMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.VoiceMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import io.prism.android.libraries.matrix.ui.messages.toPlainText
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.services.toolbox.api.strings.StringProvider

@ContributesBinding(SessionScope::class)
class DefaultPinnedMessagesBannerFormatter(
    private val sp: StringProvider,
    private val permalinkParser: PermalinkParser,
) : PinnedMessagesBannerFormatter {
    override fun format(event: EventTimelineItem): CharSequence {
        return when (val content = event.content) {
            is MessageContent -> processMessageContents(event, content)
            is StickerContent -> {
                val text = content.body ?: content.filename
                text.prefixWith(CommonStrings.common_sticker)
            }
            is UnableToDecryptContent -> {
                sp.getString(CommonStrings.common_waiting_for_decryption_key)
            }
            is PollContent -> {
                content.question.prefixWith(CommonStrings.a11y_poll)
            }
            RedactedContent -> {
                sp.getString(CommonStrings.common_message_removed)
            }
            else -> {
                sp.getString(CommonStrings.common_unsupported_event)
            }
        }
    }

    private fun processMessageContents(
        event: EventTimelineItem,
        messageContent: MessageContent,
    ): CharSequence {
        return when (val messageType: MessageType = messageContent.type) {
            is EmoteMessageType -> {
                val senderDisambiguatedDisplayName = event.senderProfile.getDisambiguatedDisplayName(event.sender)
                "* $senderDisambiguatedDisplayName ${messageType.body}"
            }
            is TextMessageType -> {
                messageType.toPlainText(permalinkParser)
            }
            is VideoMessageType -> {
                messageType.bestDescription.prefixWith(CommonStrings.common_video)
            }
            is ImageMessageType -> {
                messageType.bestDescription.prefixWith(CommonStrings.common_image)
            }
            is StickerMessageType -> {
                messageType.bestDescription.prefixWith(CommonStrings.common_sticker)
            }
            is LocationMessageType -> {
                messageType.body.prefixWith(CommonStrings.common_shared_location)
            }
            is FileMessageType -> {
                messageType.bestDescription.prefixWith(CommonStrings.common_file)
            }
            is AudioMessageType -> {
                messageType.bestDescription.prefixWith(CommonStrings.common_audio)
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
    }

    private fun CharSequence.prefixWith(@StringRes res: Int): AnnotatedString {
        val prefix = sp.getString(res)
        return prefixWith(prefix)
    }
}
