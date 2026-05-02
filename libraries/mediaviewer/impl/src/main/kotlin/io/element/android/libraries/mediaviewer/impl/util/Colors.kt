/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.theme.PRISMTheme

val bgCanvasWithTransparency: Color
    @Composable
    get() = PRISMTheme.colors.bgCanvasDefault.copy(alpha = 0.6f)
