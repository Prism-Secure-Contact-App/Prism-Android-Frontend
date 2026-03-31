/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.scan

sealed interface ScanQrCodeEvent {
    data class QrCodeScanned(val data: ByteArray) : ScanQrCodeEvent
    data object TryAgain : ScanQrCodeEvent
}
