import extension.setupDependencyInjection
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-compose-library")
}

android {
    namespace = "io.prism.android.features.roommembermoderation.impl"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.prismui)
    api(projects.features.roommembermoderation.api)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.services.analytics.compose)

    testCommonDependencies(libs, true)
    testImplementation(projects.libraries.matrix.test)
    testImplementation(projects.services.analytics.test)
    testImplementation(projects.libraries.testtags)
}
