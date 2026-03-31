/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.analytics.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.appconfig.AnalyticsConfig
import io.prism.android.features.analytics.api.AnalyticsOptInEvents
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class AnalyticsOptInPresenter(
    private val buildMeta: BuildMeta,
    private val analyticsService: AnalyticsService,
) : Presenter<AnalyticsOptInState> {
    @Composable
    override fun present(): AnalyticsOptInState {
        val localCoroutineScope = rememberCoroutineScope()

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
            localCoroutineScope.launch {
                analyticsService.setDidAskUserConsent()
            }
        }

        return AnalyticsOptInState(
            applicationName = buildMeta.applicationName,
            hasPolicyLink = AnalyticsConfig.POLICY_LINK.isNotEmpty(),
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.setIsEnabled(enabled: Boolean) = launch {
        analyticsService.setUserConsent(enabled)
    }
}
