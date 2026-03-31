/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.qrcode.intro

import io.prism.android.libraries.permissions.api.PermissionsState

data class QrCodeIntroState(
    val appName: String,
    val desktopAppName: String,
    val cameraPermissionState: PermissionsState,
    val canContinue: Boolean,
    val eventSink: (QrCodeIntroEvents) -> Unit
)
