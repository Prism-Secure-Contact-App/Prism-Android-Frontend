/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.widget

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.core.meta.BuildType
import io.prism.android.libraries.prism.api.widget.CallAnalyticCredentialsProvider
import io.prism.android.libraries.prism.api.widget.CallWidgetSettingsProvider
import io.prism.android.libraries.prism.api.widget.PRISMWidgetSettings
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.flow.first
import org.prism.rustcomponents.sdk.newVirtualPRISMCallWidget
import timber.log.Timber
import uniffi.prism_sdk.EncryptionSystem
import uniffi.prism_sdk.VirtualPRISMCallWidgetConfig
import uniffi.prism_sdk.VirtualPRISMCallWidgetProperties
import uniffi.prism_sdk.Intent as CallIntent

@ContributesBinding(AppScope::class)
class DefaultCallWidgetSettingsProvider(
    private val buildMeta: BuildMeta,
    private val callAnalyticsCredentialsProvider: CallAnalyticCredentialsProvider,
    private val analyticsService: AnalyticsService,
) : CallWidgetSettingsProvider {
    override suspend fun provide(
        baseUrl: String,
        widgetId: String,
        encrypted: Boolean,
        direct: Boolean,
        isAudioCall: Boolean,
        hasActiveCall: Boolean
    ): PRISMWidgetSettings {
        val isAnalyticsEnabled = analyticsService.userConsentFlow.first()
        val properties = VirtualPRISMCallWidgetProperties(
            prismCallUrl = baseUrl,
            widgetId = widgetId,
            fontScale = null,
            font = null,
            encryption = if (encrypted) EncryptionSystem.PerParticipantKeys else EncryptionSystem.Unencrypted,
            posthogUserId = callAnalyticsCredentialsProvider.posthogUserId.takeIf { isAnalyticsEnabled },
            posthogApiHost = callAnalyticsCredentialsProvider.posthogApiHost.takeIf { isAnalyticsEnabled },
            posthogApiKey = callAnalyticsCredentialsProvider.posthogApiKey.takeIf { isAnalyticsEnabled },
            rageshakeSubmitUrl = callAnalyticsCredentialsProvider.rageshakeSubmitUrl,
            sentryDsn = callAnalyticsCredentialsProvider.sentryDsn.takeIf { isAnalyticsEnabled },
            sentryEnvironment = if (buildMeta.buildType == BuildType.RELEASE) "RELEASE" else "DEBUG",
            parentUrl = null,
        )
        val config = VirtualPRISMCallWidgetConfig(
//            // TODO remove this once we have the next EC version
//            preload = false,
//            // TODO remove this once we have the next EC version
//            skipLobby = null,
            intent = when {
                direct && hasActiveCall -> {
                    if (isAudioCall) CallIntent.JOIN_EXISTING_DM_VOICE else CallIntent.JOIN_EXISTING_DM
                }
                hasActiveCall -> CallIntent.JOIN_EXISTING
                direct -> {
                    if (isAudioCall) CallIntent.START_CALL_DM_VOICE else CallIntent.START_CALL_DM
                }
                else -> CallIntent.START_CALL
            }.also {
                Timber.d("Starting/joining call with intent: $it")
            }
        )
        val rustWidgetSettings = newVirtualPRISMCallWidget(
            props = properties,
            config = config,
        )
        return PRISMWidgetSettings.fromRustWidgetSettings(rustWidgetSettings)
    }
}
