/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.analytics.impl.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.appconfig.AnalyticsConfig
import io.prism.android.features.analytics.api.AnalyticsOptInEvents
import io.prism.android.features.analytics.api.preferences.AnalyticsPreferencesState
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class AnalyticsPreferencesPresenter(
    private val analyticsService: AnalyticsService,
    private val buildMeta: BuildMeta,
) : Presenter<AnalyticsPreferencesState> {
    @Composable
    override fun present(): AnalyticsPreferencesState {
        val localCoroutineScope = rememberCoroutineScope()
        val isEnabled = analyticsService.userConsentFlow.collectAsState(initial = false)

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
        }

        return AnalyticsPreferencesState(
            applicationName = buildMeta.applicationName,
            isEnabled = isEnabled.value,
            policyUrl = AnalyticsConfig.POLICY_LINK,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.setIsEnabled(enabled: Boolean) = launch {
        analyticsService.setUserConsent(enabled)
    }
}
