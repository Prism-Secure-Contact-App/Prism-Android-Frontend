import extension.setupDependencyInjection
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-library")
}

setupDependencyInjection()

android {
    namespace = "io.prism.android.features.networkmonitor.impl"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    api(projects.features.networkmonitor.api)

    testCommonDependencies(libs)
    testImplementation(projects.libraries.matrix.test)
    testImplementation(projects.features.networkmonitor.test)
}
