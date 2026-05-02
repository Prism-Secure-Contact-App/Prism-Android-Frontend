/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.gallery.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.mediaviewer.impl.model.MediaItem

@Composable
fun DateItemView(
    item: MediaItem.DateSeparator,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .semantics {
                heading()
            },
        text = item.formattedDate,
        textAlign = TextAlign.Center,
        style = PRISMTheme.typography.fontBodyMdMedium,
        color = PRISMTheme.colors.textPrimary,
    )
}

@PreviewsDayNight
@Composable
internal fun DateItemViewPreview(
    @PreviewParameter(MediaItemDateSeparatorProvider::class) date: MediaItem.DateSeparator,
) = PRISMPreview {
    DateItemView(date)
}
