/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.prism.android-compose-library")
}

android {
    namespace = "io.prism.android.features.messages.test"
}

dependencies {
    api(projects.features.messages.impl)
    implementation(projects.libraries.matrix.test)
    implementation(projects.libraries.audio.test)
    implementation(projects.libraries.mediaplayer.test)
    implementation(projects.libraries.mediaupload.test)
    implementation(projects.libraries.mediaviewer.api)
    implementation(projects.libraries.permissions.test)
    implementation(projects.libraries.preferences.api)
    implementation(projects.libraries.voicerecorder.test)
    implementation(projects.services.analytics.test)
    implementation(projects.tests.testutils)
    implementation(projects.libraries.mediaupload.impl)
}



