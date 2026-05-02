/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.auth.qrlogin

import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import org.matrix.rustcomponents.sdk.QrCodeData as RustQrCodeData

class SdkQrCodeLoginData(
    internal val rustQrCodeData: RustQrCodeData,
) : PRISMQrCodeLoginData {
    override fun serverName(): String? {
        return rustQrCodeData.serverName()
    }
}
