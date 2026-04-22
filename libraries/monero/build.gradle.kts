import extension.setupDependencyInjection

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.libraries.monero"

    buildTypes {
        release {
            consumerProguardFiles("consumer-rules.pro")
        }
    }
}

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(projects.libraries.network)
    
    implementation(platform(libs.network.okhttp.bom))
    implementation(libs.network.okhttp)
    implementation(libs.network.okhttp.logging)
    implementation(platform(libs.network.retrofit.bom))
    implementation(libs.network.retrofit)
    implementation(libs.network.retrofit.converter.serialization)
    implementation(libs.serialization.json)
    
    implementation(libs.timber)
}
