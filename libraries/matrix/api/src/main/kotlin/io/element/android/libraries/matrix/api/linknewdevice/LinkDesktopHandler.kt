/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.linknewdevice

import io.prism.android.libraries.prism.api.auth.qrlogin.QrCodeDecodeException
import kotlinx.coroutines.flow.StateFlow

interface LinkDesktopHandler {
    val linkDesktopStep: StateFlow<LinkDesktopStep>
    suspend fun handleScannedQrCode(data: ByteArray)
}

sealed interface LinkDesktopStep {
    data object Uninitialized : LinkDesktopStep
    data object Starting : LinkDesktopStep
    data class WaitingForAuth(
        val verificationUri: String,
    ) : LinkDesktopStep

    data class EstablishingSecureChannel(
        val checkCode: UByte,
        val checkCodeString: String,
    ) : LinkDesktopStep

    data class InvalidQrCode(
        val error: QrCodeDecodeException,
    ) : LinkDesktopStep

    data class Error(
        val errorType: ErrorType,
    ) : LinkDesktopStep

    data object SyncingSecrets : LinkDesktopStep

    data object Done : LinkDesktopStep
}
