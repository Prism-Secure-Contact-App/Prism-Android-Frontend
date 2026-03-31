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
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.PRISMUserHeader
import io.prism.android.libraries.prism.ui.components.PRISMUserWithNullProvider

@Composable
fun UserPreferences(
    user: PRISMUser?,
    modifier: Modifier = Modifier,
) {
    PRISMUserHeader(
        modifier = modifier,
        prismUser = user
    )
}

@PreviewsDayNight
@Composable
internal fun UserPreferencesPreview(@PreviewParameter(PRISMUserWithNullProvider::class) prismUser: PRISMUser?) = PRISMPreview {
    UserPreferences(prismUser)
}
