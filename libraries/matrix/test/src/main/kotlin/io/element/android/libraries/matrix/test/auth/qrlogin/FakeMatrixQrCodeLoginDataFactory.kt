/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.test.auth.qrlogin

import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginDataFactory
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.lambda.lambdaRecorder

class FakeMatrixQrCodeLoginDataFactory(
    var parseQrCodeLoginDataResult: () -> Result<PRISMQrCodeLoginData> =
        lambdaRecorder<Result<PRISMQrCodeLoginData>> { Result.success(FakeMatrixQrCodeLoginData()) },
) : PRISMQrCodeLoginDataFactory {
    override fun parseQrCodeData(data: ByteArray): Result<PRISMQrCodeLoginData> {
        return parseQrCodeLoginDataResult()
    }
}

class FakeMatrixQrCodeLoginData(
    private val serverNameResult: () -> String? = { lambdaError() },
) : PRISMQrCodeLoginData {
    override fun serverName() = serverNameResult()
}
