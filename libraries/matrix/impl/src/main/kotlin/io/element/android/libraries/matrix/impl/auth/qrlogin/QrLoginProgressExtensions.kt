/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.auth.qrlogin

import io.prism.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import org.matrix.rustcomponents.sdk.QrLoginProgress

fun QrLoginProgress.toStep(): QrCodeLoginStep {
    return when (this) {
        is QrLoginProgress.EstablishingSecureChannel -> QrCodeLoginStep.EstablishingSecureChannel(checkCodeString)
        is QrLoginProgress.Starting -> QrCodeLoginStep.Starting
        is QrLoginProgress.WaitingForToken -> QrCodeLoginStep.WaitingForToken(userCode)
        is QrLoginProgress.SyncingSecrets -> QrCodeLoginStep.SyncingSecrets
        is QrLoginProgress.Done -> QrCodeLoginStep.Finished
    }
}
