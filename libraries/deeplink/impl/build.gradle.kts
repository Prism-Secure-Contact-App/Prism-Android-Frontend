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

android {
    namespace = "io.prism.android.libraries.deeplink.impl"
}

setupDependencyInjection()

dependencies {
    api(projects.libraries.deeplink.api)
    implementation(projects.libraries.di)
    implementation(libs.androidx.corektx)
    implementation(projects.libraries.core)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.uiStrings)
    implementation(projects.services.toolbox.api)

    testCommonDependencies(libs)
    testImplementation(projects.libraries.matrix.test)
}



