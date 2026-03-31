/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.auth.qrlogin

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.prism.api.auth.qrlogin.PRISMQrCodeLoginDataFactory
import org.prism.rustcomponents.sdk.QrCodeData

@ContributesBinding(AppScope::class)
class RustQrCodeLoginDataFactory : PRISMQrCodeLoginDataFactory {
    override fun parseQrCodeData(data: ByteArray): Result<PRISMQrCodeLoginData> {
        return runCatchingExceptions { SdkQrCodeLoginData(QrCodeData.fromBytes(data)) }
    }
}
