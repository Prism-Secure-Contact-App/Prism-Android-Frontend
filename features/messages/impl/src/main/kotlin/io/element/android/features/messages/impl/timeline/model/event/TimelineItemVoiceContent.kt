/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model.event

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.media.MediaSource
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration

data class TimelineItemVoiceContent(
    val eventId: EventId?,
    override val filename: String,
    override val fileSize: Long?,
    override val caption: String?,
    override val formattedCaption: CharSequence?,
    override val isEdited: Boolean,
    val duration: Duration,
    override val mediaSource: MediaSource,
    override val formattedFileSize: String,
    override val fileExtension: String,
    override val mimeType: String,
    val waveform: ImmutableList<Float>,
) : TimelineItemEventContentWithAttachment {
    override val type: String = "TimelineItemAudioContent"
}
