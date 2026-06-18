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
}

android {
    namespace = "io.prism.android.features.enterprise.impl"
}

setupDependencyInjection()

dependencies {
    implementation(projects.appconfig)
    implementation(projects.libraries.compound)
    api(projects.features.enterprise.api)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)

    testCommonDependencies(libs)
    testImplementation(projects.libraries.matrix.test)
}
