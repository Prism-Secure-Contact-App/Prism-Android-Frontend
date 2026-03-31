/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.featureflag.ui.model

import io.prism.android.libraries.designsystem.theme.components.IconSource

data class FeatureUiModel(
    val key: String,
    val title: String,
    val description: String?,
    val icon: IconSource?,
    val isEnabled: Boolean
)
