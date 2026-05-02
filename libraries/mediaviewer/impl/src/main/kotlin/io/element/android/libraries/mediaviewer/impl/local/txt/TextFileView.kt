/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.local.txt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.features.viewfolder.api.TextFileViewer
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.mediaviewer.api.local.LocalMedia
import io.prism.android.libraries.mediaviewer.impl.viewer.topAppBarHeight
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TextFileView(
    localMedia: LocalMedia?,
    textFileViewer: TextFileViewer,
    modifier: Modifier = Modifier,
) {
    val data = remember { mutableStateOf<AsyncData<ImmutableList<String>>>(AsyncData.Uninitialized) }
    val context = LocalContext.current
    LaunchedEffect(localMedia?.uri) {
        data.value = AsyncData.Loading()
        if (localMedia?.uri != null) {
            // Load the file content
            val result = runCatchingExceptions {
                context.contentResolver.openInputStream(localMedia.uri).use {
                    it?.bufferedReader()?.readLines()?.toList().orEmpty()
                }
            }
            data.value = if (result.isSuccess) {
                AsyncData.Success(result.getOrNull().orEmpty().toImmutableList())
            } else {
                AsyncData.Failure(result.exceptionOrNull() ?: Exception("An error occurred"))
            }
        }
    }
    TextFileContentView(
        data = data.value,
        textFileViewer = textFileViewer,
        modifier = modifier,
    )
}

@Composable
private fun TextFileContentView(
    data: AsyncData<ImmutableList<String>>,
    textFileViewer: TextFileViewer,
    modifier: Modifier = Modifier,
) {
    when (data) {
        AsyncData.Uninitialized,
        is AsyncData.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        is AsyncData.Failure -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = data.error.message ?: stringResource(id = CommonStrings.error_unknown))
        }
        is AsyncData.Success -> {
            textFileViewer.Render(
                lines = data.data,
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = topAppBarHeight),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun TextFileContentViewPreview(
    @PreviewParameter(TextFileContentProvider::class) text: AsyncData<ImmutableList<String>>,
) = PRISMPreview {
    TextFileContentView(
        data = text,
        textFileViewer = { lines, modifier ->
            Text(
                modifier = modifier,
                text = lines.firstOrNull() ?: "File content"
            )
        }
    )
}
