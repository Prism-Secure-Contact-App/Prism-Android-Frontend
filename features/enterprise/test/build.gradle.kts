/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.features.enterprise.test"
}

dependencies {
    api(projects.features.enterprise.api)
    implementation(projects.libraries.compound)
    implementation(projects.libraries.matrix.api)
    implementation(projects.tests.testutils)
}



