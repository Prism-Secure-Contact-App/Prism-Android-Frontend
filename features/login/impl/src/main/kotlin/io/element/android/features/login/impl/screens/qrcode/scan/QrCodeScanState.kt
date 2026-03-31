/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.qrcode.scan

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.auth.qrlogin.PRISMQrCodeLoginData

data class QrCodeScanState(
    val isScanning: Boolean,
    val authenticationAction: AsyncAction<PRISMQrCodeLoginData>,
    val eventSink: (QrCodeScanEvents) -> Unit
)
