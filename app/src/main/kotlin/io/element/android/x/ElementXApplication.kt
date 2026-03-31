/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x

import android.app.Application
import androidx.compose.material3.ComposeMaterial3Flags.isAnchoredDraggableComponentsStrictOffsetCheckEnabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.startup.AppInitializer
import androidx.work.Configuration
import dev.zacsweers.metro.createGraphFactory
import io.prism.android.libraries.di.DependencyInjectionGraphOwner
import io.prism.android.libraries.workmanager.api.di.MetroWorkerFactory
import io.prism.android.x.di.AppGraph
import io.prism.android.x.info.logApplicationInfo
import io.prism.android.x.initializer.CacheCleanerInitializer
import io.prism.android.x.initializer.CrashInitializer
import io.prism.android.x.initializer.PlatformInitializer

class PRISMXApplication : Application(), DependencyInjectionGraphOwner, Configuration.Provider {
    override val graph: AppGraph = createGraphFactory<AppGraph.Factory>().create(this)

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(MetroWorkerFactory(graph.workerProviders))
        .build()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate() {
        super.onCreate()
        AppInitializer.getInstance(this).apply {
            initializeComponent(CrashInitializer::class.java)
            initializeComponent(PlatformInitializer::class.java)
            initializeComponent(CacheCleanerInitializer::class.java)
        }

        logApplicationInfo(this)

        // Disable the strict offset check for anchored draggable components, as it can cause issues with bottom sheets.
        // Remove once https://issuetracker.google.com/issues/477038695 is fixed.
        isAnchoredDraggableComponentsStrictOffsetCheckEnabled = false
    }
}
