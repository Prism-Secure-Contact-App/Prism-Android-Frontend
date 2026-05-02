/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.initializer

import android.content.Context
import android.system.Os
import androidx.startup.Initializer
import io.prism.android.features.rageshake.api.logs.createWriteToFilesConfiguration
import io.prism.android.libraries.architecture.bindings
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.matrix.api.tracing.TracingConfiguration
import io.prism.android.x.di.AppBindings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

private const val PRISM_X_TARGET = "prismx"

class PlatformInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val appBindings = context.bindings<AppBindings>()
        val tracingService = appBindings.tracingService()
        val platformService = appBindings.platformService()
        val bugReporter = appBindings.bugReporter()
        Timber.plant(tracingService.createTimberTree(PRISM_X_TARGET))
        val preferencesStore = appBindings.preferencesStore()
        val featureFlagService = appBindings.featureFlagService()
        val logLevel = runBlocking { preferencesStore.getTracingLogLevelFlow().first() }
        val tracingConfiguration = TracingConfiguration(
            writesToLogcat = runBlocking { featureFlagService.isFeatureEnabled(FeatureFlags.PrintLogsToLogcat) },
            writesToFilesConfiguration = bugReporter.createWriteToFilesConfiguration(),
            logLevel = logLevel,
            extraTargets = listOf(PRISM_X_TARGET),
            traceLogPacks = runBlocking { preferencesStore.getTracingLogPacksFlow().first() },
            sdkSentryDsn = appBindings.sentrySdkDsn()?.value?.takeIf { it.isNotBlank() },
        )
        bugReporter.setCurrentTracingLogLevel(logLevel.name)
        platformService.init(tracingConfiguration)
        // Also set env variable for rust back trace
        Os.setenv("RUST_BACKTRACE", "1", true)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = mutableListOf()
}
