/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.developer

import androidx.compose.ui.graphics.Color
import io.prism.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.prism.android.libraries.featureflag.ui.model.FeatureUiModel
import io.prism.android.libraries.prism.api.tracing.TraceLogPack

sealed interface DeveloperSettingsEvents {
    data class UpdateEnabledFeature(val feature: FeatureUiModel, val isEnabled: Boolean) : DeveloperSettingsEvents
    data class SetCustomPRISMCallBaseUrl(val baseUrl: String?) : DeveloperSettingsEvents
    data class SetTracingLogLevel(val logLevel: LogLevelItem) : DeveloperSettingsEvents
    data class ToggleTracingLogPack(val logPack: TraceLogPack, val enabled: Boolean) : DeveloperSettingsEvents
    data class SetShowColorPicker(val show: Boolean) : DeveloperSettingsEvents
    data class ChangeBrandColor(val color: Color?) : DeveloperSettingsEvents
    data object ClearCache : DeveloperSettingsEvents
    data object VacuumStores : DeveloperSettingsEvents
}
