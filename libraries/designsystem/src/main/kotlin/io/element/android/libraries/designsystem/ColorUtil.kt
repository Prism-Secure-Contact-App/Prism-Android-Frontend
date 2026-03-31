/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.theme.PRISMTheme

@Composable
fun Boolean.toEnabledColor(): Color {
    return if (this) {
        PRISMTheme.colors.textPrimary
    } else {
        PRISMTheme.colors.textDisabled
    }
}

@Composable
fun Boolean.toSecondaryEnabledColor(): Color {
    return if (this) {
        PRISMTheme.colors.textSecondary
    } else {
        PRISMTheme.colors.textDisabled
    }
}

@Composable
fun Boolean.toIconEnabledColor(): Color {
    return if (this) {
        PRISMTheme.colors.iconPrimary
    } else {
        PRISMTheme.colors.iconDisabled
    }
}

@Composable
fun Boolean.toIconSecondaryEnabledColor(): Color {
    return if (this) {
        PRISMTheme.colors.iconSecondary
    } else {
        PRISMTheme.colors.iconDisabled
    }
}
