/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.local.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.utils.CommonDrawables
import io.prism.android.libraries.mediaviewer.api.local.LocalMedia
import io.prism.android.libraries.mediaviewer.impl.local.LocalMediaViewState
import io.prism.android.libraries.mediaviewer.impl.local.rememberLocalMediaViewState
import io.prism.android.libraries.ui.strings.CommonStrings
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState

@Composable
fun MediaImageView(
    localMediaViewState: LocalMediaViewState,
    localMedia: LocalMedia?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(id = CommonDrawables.sample_background),
            modifier = modifier,
            contentDescription = null,
        )
    } else {
        val zoomableImageState = rememberZoomableImageState(localMediaViewState.zoomableState)
        localMediaViewState.isReady = zoomableImageState.isImageDisplayed
        ZoomableAsyncImage(
            modifier = modifier,
            state = zoomableImageState,
            model = localMedia?.uri,
            contentDescription = stringResource(id = CommonStrings.common_image),
            contentScale = ContentScale.Fit,
            onClick = { onClick() }
        )
    }
}

@PreviewsDayNight
@Composable
internal fun MediaImageViewPreview() = PRISMPreview {
    MediaImageView(
        modifier = Modifier.fillMaxSize(),
        localMediaViewState = rememberLocalMediaViewState(),
        localMedia = null,
        onClick = {},
    )
}
