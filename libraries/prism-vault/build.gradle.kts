/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.libraries.vault"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.datastore.preferences)
    implementation(projects.libraries.di)
}



