/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.compound.colors

import io.prism.android.compound.tokens.generated.SemanticColors
import io.prism.android.compound.tokens.generated.compoundColorsDark
import io.prism.android.compound.tokens.generated.compoundColorsLight

data class SemanticColorsLightDark(
    val light: SemanticColors,
    val dark: SemanticColors,
) {
    companion object {
        val default = SemanticColorsLightDark(
            light = compoundColorsLight,
            dark = compoundColorsDark,
        )
    }
}
