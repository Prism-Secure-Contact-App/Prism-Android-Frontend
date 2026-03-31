/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.libraries.lightning"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.breez.sdk)
    implementation(projects.libraries.di)
}
