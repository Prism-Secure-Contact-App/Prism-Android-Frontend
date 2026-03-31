/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.utils

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.androidutils.browser.openUrlInChromeCustomTab

@Suppress("MutableStateParam")
@Composable
fun OpenUrlInTabView(url: MutableState<String?>) {
    val activity = requireNotNull(LocalActivity.current)
    val darkTheme = PRISMTheme.isLightTheme.not()

    LaunchedEffect(url.value) {
        url.value?.let {
            activity.openUrlInChromeCustomTab(null, darkTheme, it)
            url.value = null
        }
    }
}
