/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.preview

import androidx.compose.runtime.Composable

@Composable
fun PRISMPreviewLight(
    showBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    PRISMPreview(
        darkTheme = false,
        showBackground = showBackground,
        content = content
    )
}
