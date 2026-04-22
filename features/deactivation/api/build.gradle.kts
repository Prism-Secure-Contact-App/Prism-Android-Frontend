/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("io.prism.android-compose-library")
}

android {
    namespace = "io.prism.android.features.deactivation.api"
}

dependencies {
    implementation(projects.libraries.architecture)
}



