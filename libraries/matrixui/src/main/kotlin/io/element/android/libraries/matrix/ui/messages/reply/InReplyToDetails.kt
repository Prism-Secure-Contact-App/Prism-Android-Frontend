/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.messages.reply

import androidx.compose.runtime.Immutable
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.permalink.PermalinkParser
import io.prism.android.libraries.matrix.api.timeline.item.event.EventContent
import io.prism.android.libraries.matrix.api.timeline.item.event.InReplyTo
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageContent
import io.prism.android.libraries.matrix.api.timeline.item.event.ProfileDetails
import io.prism.android.libraries.matrix.api.timeline.item.event.StickerContent
import io.prism.android.libraries.matrix.api.timeline.item.event.TextMessageType
import io.prism.android.libraries.matrix.ui.messages.toPlainText

@Immutable
sealed interface InReplyToDetails {
    data class Ready(
        val eventId: EventId,
        val senderId: UserId,
        val senderProfile: ProfileDetails,
        val eventContent: EventContent?,
        val textContent: String?,
    ) : InReplyToDetails

    data class Loading(val eventId: EventId) : InReplyToDetails
    data class Error(val eventId: EventId, val message: String) : InReplyToDetails
}

fun InReplyToDetails.eventId() = when (this) {
    is InReplyToDetails.Ready -> eventId
    is InReplyToDetails.Loading -> eventId
    is InReplyToDetails.Error -> eventId
}

fun InReplyTo.map(
    permalinkParser: PermalinkParser,
) = when (this) {
    is InReplyTo.Ready -> InReplyToDetails.Ready(
        eventId = eventId,
        senderId = senderId,
        senderProfile = senderProfile,
        eventContent = content,
        textContent = when (content) {
            is MessageContent -> {
                val messageContent = content as MessageContent
                (messageContent.type as? TextMessageType)?.toPlainText(permalinkParser = permalinkParser) ?: messageContent.body
            }
            is StickerContent -> {
                val stickerContent = content as StickerContent
                stickerContent.body
            }
            else -> null
        }
    )
    is InReplyTo.Error -> InReplyToDetails.Error(eventId, message)
    is InReplyTo.NotLoaded -> InReplyToDetails.Loading(eventId)
    is InReplyTo.Pending -> InReplyToDetails.Loading(eventId)
}
