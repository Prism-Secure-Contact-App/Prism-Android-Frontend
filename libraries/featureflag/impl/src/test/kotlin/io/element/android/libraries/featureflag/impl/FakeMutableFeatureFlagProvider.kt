/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.featureflag.impl

import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.featureflag.api.Feature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMutableFeatureFlagProvider(
    override val priority: Int,
    private val buildMeta: BuildMeta,
) : MutableFeatureFlagProvider {
    private val enabledFeatures = mutableMapOf<String, MutableStateFlow<Boolean>>()

    override suspend fun setFeatureEnabled(feature: Feature, enabled: Boolean) {
        val flow = enabledFeatures.getOrPut(feature.key) { MutableStateFlow(enabled) }
        flow.emit(enabled)
    }

    override fun isFeatureEnabledFlow(feature: Feature): Flow<Boolean> {
        return enabledFeatures.getOrPut(feature.key) { MutableStateFlow(feature.defaultValue(buildMeta)) }
    }

    override fun hasFeature(feature: Feature): Boolean = true
}
