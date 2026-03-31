/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.theme.PRISMTheme

@Composable
@ReadOnlyComposable
fun gradientActionColors(): List<Color> = listOf(
    PRISMTheme.colors.gradientActionStop1,
    PRISMTheme.colors.gradientActionStop2,
    PRISMTheme.colors.gradientActionStop3,
    PRISMTheme.colors.gradientActionStop4,
)

@Composable
@ReadOnlyComposable
fun gradientSubtleColors(): List<Color> = listOf(
    PRISMTheme.colors.gradientSubtleStop1,
    PRISMTheme.colors.gradientSubtleStop2,
    PRISMTheme.colors.gradientSubtleStop3,
    PRISMTheme.colors.gradientSubtleStop4,
    PRISMTheme.colors.gradientSubtleStop5,
    PRISMTheme.colors.gradientSubtleStop6,
)

@Composable
@ReadOnlyComposable
fun gradientInfoColors(): List<Color> = listOf(
    PRISMTheme.colors.gradientInfoStop1,
    PRISMTheme.colors.gradientInfoStop2,
)

@Composable
@ReadOnlyComposable
fun gradientCriticalColors(): List<Color> = listOf(
    PRISMTheme.colors.gradientCriticalStop1,
    PRISMTheme.colors.gradientCriticalStop2,
)
