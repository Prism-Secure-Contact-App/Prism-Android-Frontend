/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.datasource

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.androidutils.filesize.FileSizeFormatter
import io.prism.android.libraries.dateformatter.api.DateFormatter
import io.prism.android.libraries.dateformatter.api.DateFormatterMode
import io.prism.android.libraries.dateformatter.api.toHumanReadableDuration
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.AudioMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.CallNotifyContent
import io.prism.android.libraries.matrix.api.timeline.item.event.EmoteMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.FailedToParseMessageLikeContent
import io.prism.android.libraries.matrix.api.timeline.item.event.FailedToParseStateContent
import io.prism.android.libraries.matrix.api.timeline.item.event.FileMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.ImageMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.LegacyCallInviteContent
import io.prism.android.libraries.matrix.api.timeline.item.event.LiveLocationContent
import io.prism.android.libraries.matrix.api.timeline.item.event.LocationMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageContent
import io.prism.android.libraries.matrix.api.timeline.item.event.NoticeMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.OtherMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.PollContent
import io.prism.android.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import io.prism.android.libraries.matrix.api.timeline.item.event.RedactedContent
import io.prism.android.libraries.matrix.api.timeline.item.event.RoomMembershipContent
import io.prism.android.libraries.matrix.api.timeline.item.event.StateContent
import io.prism.android.libraries.matrix.api.timeline.item.event.StickerContent
import io.prism.android.libraries.matrix.api.timeline.item.event.StickerMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.TextMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import io.prism.android.libraries.matrix.api.timeline.item.event.UnknownContent
import io.prism.android.libraries.matrix.api.timeline.item.event.VideoMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.VoiceMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.getAvatarUrl
import io.prism.android.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import io.prism.android.libraries.mediaviewer.api.MediaInfo
import io.prism.android.libraries.mediaviewer.api.util.FileExtensionExtractor
import io.prism.android.libraries.mediaviewer.impl.model.MediaItem
import timber.log.Timber

@Inject
class EventItemFactory(
    private val fileSizeFormatter: FileSizeFormatter,
    private val fileExtensionExtractor: FileExtensionExtractor,
    private val dateFormatter: DateFormatter,
) {
    fun create(
        currentTimelineItem: PRISMTimelineItem.Event,
    ): MediaItem.Event? {
        val event = currentTimelineItem.event
        val dateSent = dateFormatter.format(
            currentTimelineItem.event.timestamp,
            mode = DateFormatterMode.Day,
        )
        val dateSentFull = dateFormatter.format(
            timestamp = currentTimelineItem.event.timestamp,
            mode = DateFormatterMode.Full,
        )
        return when (val content = event.content) {
            CallNotifyContent,
            is FailedToParseMessageLikeContent,
            is FailedToParseStateContent,
            LegacyCallInviteContent,
            is PollContent,
            is ProfileChangeContent,
            RedactedContent,
            is RoomMembershipContent,
            is StateContent,
            is StickerContent,
            is UnableToDecryptContent,
            is LiveLocationContent,
            UnknownContent -> {
                Timber.w("Should not happen: ${content.javaClass.simpleName}")
                null
            }
            is MessageContent -> {
                when (val type = content.type) {
                    is EmoteMessageType,
                    is NoticeMessageType,
                    is OtherMessageType,
                    is LocationMessageType,
                    is TextMessageType -> {
                        Timber.w("Should not happen: ${content.type}")
                        null
                    }
                    is AudioMessageType -> MediaItem.Audio(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                    )
                    is FileMessageType -> MediaItem.File(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        // TODO We may want to add a thumbnailSource and set it to type.info?.thumbnailSource
                    )
                    is ImageMessageType -> MediaItem.Image(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is StickerMessageType -> MediaItem.Image(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is VideoMessageType -> MediaItem.Video(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = type.info?.duration?.inWholeMilliseconds?.toHumanReadableDuration(),
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is VoiceMessageType -> MediaItem.Voice(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = type.details?.waveform.orEmpty(),
                            duration = type.info?.duration?.inWholeMilliseconds?.toHumanReadableDuration(),
                        ),
                        mediaSource = type.source,
                    )
                }
            }
        }
    }
}
