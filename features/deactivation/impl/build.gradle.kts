import extension.setupDependencyInjection
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-compose-library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.prism.android.features.deactivation.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.testtags)
    implementation(projects.libraries.uiStrings)
    api(projects.features.deactivation.api)

    testCommonDependencies(libs, true)
    testImplementation(projects.libraries.matrix.test)
}
