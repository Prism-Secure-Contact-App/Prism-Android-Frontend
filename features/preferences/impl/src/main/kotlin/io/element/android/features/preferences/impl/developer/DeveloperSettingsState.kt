/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.developer

import io.prism.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.prism.android.features.rageshake.api.preferences.RageshakePreferencesState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.featureflag.ui.model.FeatureUiModel
import io.prism.android.libraries.prism.api.tracing.TraceLogPack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class DeveloperSettingsState(
    val features: ImmutableList<FeatureUiModel>,
    val cacheSize: AsyncData<String>,
    val databaseSizes: AsyncData<ImmutableMap<String, String>>,
    val rageshakeState: RageshakePreferencesState,
    val clearCacheAction: AsyncAction<Unit>,
    val customPRISMCallBaseUrlState: CustomPRISMCallBaseUrlState,
    val tracingLogLevel: AsyncData<LogLevelItem>,
    val tracingLogPacks: ImmutableList<TraceLogPack>,
    val isEnterpriseBuild: Boolean,
    val showColorPicker: Boolean,
    val eventSink: (DeveloperSettingsEvents) -> Unit
) {
    val showLoader = clearCacheAction is AsyncAction.Loading
}

data class CustomPRISMCallBaseUrlState(
    val baseUrl: String?,
    val validator: (String?) -> Boolean,
)
