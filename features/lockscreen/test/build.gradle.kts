/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.features.lockscreen.test"
}

dependencies {
    api(projects.features.lockscreen.api)
    implementation(libs.coroutines.core)
    implementation(projects.libraries.architecture)
    implementation(projects.tests.testutils)
}



