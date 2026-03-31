/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

import extension.setupDependencyInjection

plugins {
    id("io.prism.android-compose-library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.prism.android.features.lightning.impl"
}

setupDependencyInjection()

dependencies {
    api(projects.features.lightning.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.uiCommon)
    implementation(projects.libraries.prismLightning)
    implementation(libs.coroutines.core)
}
