/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.compound.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.compound.previews.ColorsSchemePreview
import io.prism.android.compound.tokens.generated.SemanticColors
import io.prism.android.compound.tokens.generated.compoundColorsHcDark
import io.prism.android.compound.tokens.generated.compoundColorsHcLight

fun SemanticColors.toMaterialColorScheme(): ColorScheme {
    return if (isLight) {
        toMaterialColorSchemeLight()
    } else {
        toMaterialColorSchemeDark()
    }
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeLightPreview() = PRISMTheme {
    ColorsSchemePreview(
        Color.Black,
        Color.White,
        PRISMTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeLightHcPreview() = PRISMTheme(
    compoundLight = compoundColorsHcLight,
) {
    ColorsSchemePreview(
        Color.Black,
        Color.White,
        PRISMTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeDarkPreview() = PRISMTheme(
    darkTheme = true,
) {
    ColorsSchemePreview(
        Color.White,
        Color.Black,
        PRISMTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeDarkHcPreview() = PRISMTheme(
    darkTheme = true,
    compoundDark = compoundColorsHcDark,
) {
    ColorsSchemePreview(
        Color.White,
        Color.Black,
        PRISMTheme.materialColors,
    )
}
