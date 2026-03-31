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
    namespace = "io.prism.android.features.vault.impl"
}

setupDependencyInjection()

dependencies {
    api(projects.features.vault.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.uiCommon)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.prismBridge)
    implementation(projects.libraries.prismVault)
    implementation(libs.androidx.biometric)
    implementation(libs.coroutines.core)
}
