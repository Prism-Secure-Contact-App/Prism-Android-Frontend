/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.local

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.prism.android.features.viewfolder.api.TextFileViewer
import io.prism.android.libraries.audio.api.AudioFocus
import io.prism.android.libraries.core.mimetype.MimeTypes
import io.prism.android.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import io.prism.android.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import io.prism.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.prism.android.libraries.mediaviewer.api.MediaInfo
import io.prism.android.libraries.mediaviewer.api.local.LocalMedia
import io.prism.android.libraries.mediaviewer.impl.local.audio.MediaAudioView
import io.prism.android.libraries.mediaviewer.impl.local.file.MediaFileView
import io.prism.android.libraries.mediaviewer.impl.local.image.MediaImageView
import io.prism.android.libraries.mediaviewer.impl.local.pdf.MediaPdfView
import io.prism.android.libraries.mediaviewer.impl.local.txt.TextFileView
import io.prism.android.libraries.mediaviewer.impl.local.video.MediaVideoView

@Composable
fun LocalMediaView(
    localMedia: LocalMedia?,
    bottomPaddingInPixels: Int,
    audioFocus: AudioFocus?,
    onClick: () -> Unit,
    textFileViewer: TextFileViewer,
    modifier: Modifier = Modifier,
    isDisplayed: Boolean = true,
    isUserSelected: Boolean = false,
    localMediaViewState: LocalMediaViewState = rememberLocalMediaViewState(),
    mediaInfo: MediaInfo? = localMedia?.info,
) {
    val mimeType = mediaInfo?.mimeType
    when {
        mimeType.isMimeTypeImage() -> MediaImageView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            modifier = modifier,
            onClick = onClick,
        )
        mimeType.isMimeTypeVideo() -> MediaVideoView(
            isDisplayed = isDisplayed,
            localMediaViewState = localMediaViewState,
            bottomPaddingInPixels = bottomPaddingInPixels,
            localMedia = localMedia,
            autoplay = isUserSelected,
            audioFocus = audioFocus,
            modifier = modifier,
        )
        mimeType == MimeTypes.PlainText -> TextFileView(
            localMedia = localMedia,
            textFileViewer = textFileViewer,
            modifier = modifier,
        )
        mimeType == MimeTypes.Pdf -> MediaPdfView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            modifier = modifier,
            onClick = onClick,
        )
        mimeType.isMimeTypeAudio() -> MediaAudioView(
            isDisplayed = isDisplayed,
            localMediaViewState = localMediaViewState,
            bottomPaddingInPixels = bottomPaddingInPixels,
            localMedia = localMedia,
            info = mediaInfo,
            audioFocus = audioFocus,
            modifier = modifier,
        )
        else -> MediaFileView(
            localMediaViewState = localMediaViewState,
            uri = localMedia?.uri,
            info = mediaInfo,
            modifier = modifier,
            onClick = onClick,
        )
    }
}
