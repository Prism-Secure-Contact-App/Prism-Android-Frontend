/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.qrcode.scan

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.login.impl.changeserver.AccountProviderAccessException
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrLoginException

open class QrCodeScanStateProvider : PreviewParameterProvider<QrCodeScanState> {
    override val values: Sequence<QrCodeScanState>
        get() = sequenceOf(
            aQrCodeScanState(),
            aQrCodeScanState(isScanning = false, authenticationAction = AsyncAction.Loading),
            aQrCodeScanState(isScanning = false, authenticationAction = AsyncAction.Failure(Exception("Error"))),
            aQrCodeScanState(isScanning = false, authenticationAction = AsyncAction.Failure(QrLoginException.OtherDeviceNotSignedIn)),
            aQrCodeScanState(
                isScanning = false,
                authenticationAction = AsyncAction.Failure(
                    AccountProviderAccessException.UnauthorizedAccountProviderException(
                        unauthorisedAccountProviderTitle = "example.com",
                        authorisedAccountProviderTitles = listOf("prism.io", "prism.org"),
                    )
                )
            ),
            aQrCodeScanState(
                isScanning = false,
                authenticationAction = AsyncAction.Failure(
                    AccountProviderAccessException.NeedPRISMProException(
                        unauthorisedAccountProviderTitle = "example.com",
                        applicationId = "applicationId"
                    )
                )
            ),
            // Add other state here
        )
}

fun aQrCodeScanState(
    isScanning: Boolean = true,
    authenticationAction: AsyncAction<PRISMQrCodeLoginData> = AsyncAction.Uninitialized,
    eventSink: (QrCodeScanEvents) -> Unit = {},
) = QrCodeScanState(
    isScanning = isScanning,
    authenticationAction = authenticationAction,
    eventSink = eventSink
)
