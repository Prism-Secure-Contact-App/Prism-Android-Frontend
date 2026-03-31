/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.labs

import io.prism.android.libraries.featureflag.ui.model.FeatureUiModel

sealed interface LabsEvents {
    data class ToggleFeature(val feature: FeatureUiModel) : LabsEvents
}
