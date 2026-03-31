/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model.event

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.core.mimetype.MimeTypes
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.media.MediaSource
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

open class TimelineItemVoiceContentProvider : PreviewParameterProvider<TimelineItemVoiceContent> {
    override val values: Sequence<TimelineItemVoiceContent>
        get() = sequenceOf(
            aTimelineItemVoiceContent(
                duration = 1.milliseconds,
                waveform = listOf(),
            ),
            aTimelineItemVoiceContent(
                duration = 10_000.milliseconds,
                waveform = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 8f, 7f, 6f, 5f, 4f, 3f, 2f, 1f, 0f),
            ),
            aTimelineItemVoiceContent(
                duration = 30.minutes,
                waveform = List(1024) { it / 1024f },
            ),
        )
}

fun aTimelineItemVoiceContent(
    eventId: EventId? = EventId("\$anEventId"),
    filename: String = "filename doesn't really matter for a voice message",
    caption: String? = "body doesn't really matter for a voice message",
    duration: Duration = 61_000.milliseconds,
    contentUri: String = "mxc://prism.org/1234567890abcdefg",
    mimeType: String = MimeTypes.Ogg,
    mediaSource: MediaSource = MediaSource(contentUri),
    waveform: List<Float> = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 8f, 7f, 6f, 5f, 4f, 3f, 2f, 1f, 0f),
) = TimelineItemVoiceContent(
    eventId = eventId,
    fileSize = 1024 * 1024,
    filename = filename,
    caption = caption,
    formattedCaption = null,
    isEdited = false,
    duration = duration,
    mediaSource = mediaSource,
    mimeType = mimeType,
    waveform = waveform.toImmutableList(),
    formattedFileSize = "1.0 MB",
    fileExtension = "ogg",
)
