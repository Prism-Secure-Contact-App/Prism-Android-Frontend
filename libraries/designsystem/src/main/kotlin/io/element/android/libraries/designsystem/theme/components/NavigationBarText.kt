/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.theme.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.prism.android.compound.theme.PRISMTheme

@Composable
fun NavigationBarText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = PRISMTheme.typography.fontBodySmMedium,
    )
}
