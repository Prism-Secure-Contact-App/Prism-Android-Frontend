import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-compose-library")
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.prism.android.libraries.prism.api"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr(
            name = "CLIENT_URI",
            value = BuildTimeConfig.URL_WEBSITE ?: "https://prism.io"
        )
        buildConfigFieldStr(
            name = "LOGO_URI",
            value = BuildTimeConfig.URL_LOGO ?: "https://prism.io/mobile-icon.png"
        )
        buildConfigFieldStr(
            name = "TOS_URI",
            value = BuildTimeConfig.URL_ACCEPTABLE_USE ?: "https://prism.io/acceptable-use-policy-terms"
        )
        buildConfigFieldStr(
            name = "POLICY_URI",
            value = BuildTimeConfig.URL_POLICY ?: "https://prism.io/privacy"
        )
    }
}

dependencies {
    implementation(projects.libraries.di)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.services.analytics.api)
    implementation(libs.serialization.json)
    api(projects.libraries.sessionStorage.api)
    implementation(libs.coroutines.core)
    api(projects.libraries.architecture)

    testCommonDependencies(libs)
    testImplementation(projects.libraries.matrix.test)
}



