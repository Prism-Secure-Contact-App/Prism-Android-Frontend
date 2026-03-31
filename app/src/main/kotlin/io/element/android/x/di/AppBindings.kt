/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.prism.android.features.api.MigrationEntryPoint
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.lockscreen.api.LockScreenEntryPoint
import io.prism.android.features.lockscreen.api.LockScreenService
import io.prism.android.features.rageshake.api.reporter.BugReporter
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.prism.android.libraries.di.identifiers.SentrySdkDsn
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.prism.api.platform.InitPlatformService
import io.prism.android.libraries.prism.api.tracing.TracingService
import io.prism.android.libraries.preferences.api.store.AppPreferencesStore
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesTo(AppScope::class)
interface AppBindings {
    fun snackbarDispatcher(): SnackbarDispatcher

    fun tracingService(): TracingService

    fun platformService(): InitPlatformService

    fun bugReporter(): BugReporter

    fun lockScreenService(): LockScreenService

    fun preferencesStore(): AppPreferencesStore

    fun migrationEntryPoint(): MigrationEntryPoint

    fun lockScreenEntryPoint(): LockScreenEntryPoint

    fun analyticsService(): AnalyticsService

    fun enterpriseService(): EnterpriseService

    fun featureFlagService(): FeatureFlagService

    fun buildMeta(): BuildMeta

    fun sentrySdkDsn(): SentrySdkDsn?
}
