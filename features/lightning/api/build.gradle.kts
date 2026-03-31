/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

plugins {
    id("io.prism.android-compose-library")
}

android {
    namespace = "io.prism.android.features.lightning.api"
}

dependencies {
    implementation(projects.libraries.architecture)
}
