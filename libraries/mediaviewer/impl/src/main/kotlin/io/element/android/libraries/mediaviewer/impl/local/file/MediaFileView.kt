/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.local.file

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.mediaviewer.api.MediaInfo
import io.prism.android.libraries.mediaviewer.api.helper.formatFileExtensionAndSize
import io.prism.android.libraries.mediaviewer.impl.local.LocalMediaViewState
import io.prism.android.libraries.mediaviewer.impl.local.rememberLocalMediaViewState

@Composable
fun MediaFileView(
    localMediaViewState: LocalMediaViewState,
    uri: Uri?,
    info: MediaInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAudio = info?.mimeType.isMimeTypeAudio().orFalse()
    localMediaViewState.isReady = uri != null

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PRISMTheme.colors.iconPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isAudio) CompoundIcons.Audio() else CompoundIcons.Attachment(),
                    contentDescription = null,
                    tint = PRISMTheme.colors.iconOnSolidPrimary,
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(if (isAudio) 0f else -45f),
                )
            }
            if (info != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = info.filename,
                    maxLines = 2,
                    style = PRISMTheme.typography.fontBodyLgRegular,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = PRISMTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatFileExtensionAndSize(info.fileExtension, info.formattedFileSize),
                    style = PRISMTheme.typography.fontBodyMdRegular,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = PRISMTheme.colors.textPrimary
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun MediaFileViewPreview(
    @PreviewParameter(MediaInfoFileProvider::class) info: MediaInfo
) = PRISMPreview {
    MediaFileView(
        modifier = Modifier.fillMaxSize(),
        localMediaViewState = rememberLocalMediaViewState(),
        uri = null,
        info = info,
        onClick = {},
    )
}
