/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.components.MatrixUserHeader
import io.prism.android.libraries.matrix.ui.components.MatrixUserWithNullProvider

@Composable
fun UserPreferences(
    user: PRISMUser?,
    modifier: Modifier = Modifier,
) {
    MatrixUserHeader(
        modifier = modifier,
        matrixUser = user
    )
}

@PreviewsDayNight
@Composable
internal fun UserPreferencesPreview(@PreviewParameter(MatrixUserWithNullProvider::class) matrixUser: PRISMUser?) = PRISMPreview {
    UserPreferences(matrixUser)
}
