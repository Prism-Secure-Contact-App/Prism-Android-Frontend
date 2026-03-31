/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.qrcode

import io.prism.android.libraries.prism.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.prism.api.auth.qrlogin.QrCodeLoginStep
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.flow.MutableStateFlow

class FakeQrCodeLoginManager(
    var authenticateResult: (PRISMQrCodeLoginData) -> Result<SessionId> =
        lambdaRecorder<PRISMQrCodeLoginData, Result<SessionId>> { Result.success(A_SESSION_ID) },
    var resetAction: () -> Unit = lambdaRecorder<Unit> { },
) : QrCodeLoginManager {
    override val currentLoginStep: MutableStateFlow<QrCodeLoginStep> =
        MutableStateFlow(QrCodeLoginStep.Uninitialized)

    override suspend fun authenticate(qrCodeLoginData: PRISMQrCodeLoginData): Result<SessionId> {
        return authenticateResult(qrCodeLoginData)
    }

    override fun reset() {
        resetAction()
    }
}
