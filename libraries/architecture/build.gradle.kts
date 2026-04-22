import extension.setupDependencyInjection
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("io.prism.android-compose-library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.prism.android.libraries.architecture"
}

setupDependencyInjection()

dependencies {
    api(projects.libraries.di)
    api(projects.libraries.core)
    api(libs.metro.runtime)
    api(libs.appyx.core)
    api(libs.androidx.lifecycle.runtime)
    api(libs.molecule.runtime)

    testCommonDependencies(libs)
}



