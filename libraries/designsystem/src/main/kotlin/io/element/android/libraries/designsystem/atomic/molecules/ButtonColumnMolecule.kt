/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.OutlinedButton
import io.prism.android.libraries.designsystem.theme.components.TextButton

@Composable
fun ButtonColumnMolecule(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()
    }
}

@PreviewsDayNight
@Composable
internal fun ButtonColumnMoleculePreview() = PRISMPreview {
    ButtonColumnMolecule {
        Button(text = "Button", onClick = {}, modifier = Modifier.fillMaxWidth())
        OutlinedButton(text = "OutlinedButton", onClick = {}, modifier = Modifier.fillMaxWidth())
        TextButton(text = "TextButton", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
