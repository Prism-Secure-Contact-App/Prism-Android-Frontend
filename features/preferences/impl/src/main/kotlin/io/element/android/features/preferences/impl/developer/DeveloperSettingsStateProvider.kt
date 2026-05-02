/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.developer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.prism.android.features.rageshake.api.preferences.aRageshakePreferencesState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.featureflag.ui.model.aFeatureUiModelList
import io.prism.android.libraries.matrix.api.tracing.TraceLogPack
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

open class DeveloperSettingsStateProvider : PreviewParameterProvider<DeveloperSettingsState> {
    override val values: Sequence<DeveloperSettingsState>
        get() = sequenceOf(
            aDeveloperSettingsState(),
            aDeveloperSettingsState(
                clearCacheAction = AsyncAction.Loading
            ),
            aDeveloperSettingsState(
                customPRISMCallBaseUrlState = aCustomPRISMCallBaseUrlState(
                    baseUrl = "https://call.prism.ahoy",
                )
            ),
            aDeveloperSettingsState(
                isEnterpriseBuild = true,
                // Disable the color picker for now, Paparazzi is failing with:
                // java.lang.IllegalArgumentException: Cannot round NaN value.
                //  at kotlin.math.MathKt__MathJVMKt.roundToInt(MathJVM.kt:1210)
                //  at io.mhssn.colorpicker.ext.ColorExtKt.lighten(ColorExt.kt:86)
                //  at io.mhssn.colorpicker.pickers.ClassicColorPickerKt$ClassicColorPicker$1$1.invokeSuspend(ClassicColorPicker.kt:53)
                showColorPicker = false,
            ),
        )
}

fun aDeveloperSettingsState(
    clearCacheAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    customPRISMCallBaseUrlState: CustomPRISMCallBaseUrlState = aCustomPRISMCallBaseUrlState(),
    traceLogPacks: List<TraceLogPack> = emptyList(),
    isEnterpriseBuild: Boolean = false,
    showColorPicker: Boolean = false,
    eventSink: (DeveloperSettingsEvents) -> Unit = {},
) = DeveloperSettingsState(
    features = aFeatureUiModelList(),
    rageshakeState = aRageshakePreferencesState(),
    cacheSize = AsyncData.Success("1.2 MB"),
    databaseSizes = AsyncData.Success(persistentMapOf("state_store" to "1.2MB")),
    clearCacheAction = clearCacheAction,
    customPRISMCallBaseUrlState = customPRISMCallBaseUrlState,
    tracingLogLevel = AsyncData.Success(LogLevelItem.INFO),
    tracingLogPacks = traceLogPacks.toImmutableList(),
    isEnterpriseBuild = isEnterpriseBuild,
    showColorPicker = showColorPicker,
    eventSink = eventSink,
)

fun aCustomPRISMCallBaseUrlState(
    baseUrl: String? = null,
    validator: (String?) -> Boolean = { true },
) = CustomPRISMCallBaseUrlState(
    baseUrl = baseUrl,
    validator = validator,
)
