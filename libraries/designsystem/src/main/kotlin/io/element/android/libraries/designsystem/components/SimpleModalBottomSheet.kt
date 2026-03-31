/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.prism.android.libraries.designsystem.theme.components.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleModalBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                title,
                style = PRISMTheme.typography.fontBodyLgMedium,
                color = PRISMTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@PreviewsDayNight
@Composable
internal fun SimpleModalBottomSheetPreview() = PRISMPreview {
    SimpleModalBottomSheet(title = "A title", onDismiss = {}) {
        Text(
            text = LoremIpsum(20).values.first(),
            color = PRISMTheme.colors.textSecondary,
            style = PRISMTheme.typography.fontBodyMdRegular,
        )
    }
}
