/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.qrcode

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.prism.android.features.login.impl.di.QrCodeLoginScope
import io.prism.android.libraries.matrix.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrLoginException
import io.prism.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SingleIn(QrCodeLoginScope::class)
@ContributesBinding(QrCodeLoginScope::class)
class DefaultQrCodeLoginManager(
    private val authenticationService: PRISMAuthenticationService,
) : QrCodeLoginManager {
    private val _currentLoginStep = MutableStateFlow<QrCodeLoginStep>(QrCodeLoginStep.Uninitialized)
    override val currentLoginStep: StateFlow<QrCodeLoginStep> = _currentLoginStep

    override suspend fun authenticate(qrCodeLoginData: PRISMQrCodeLoginData): Result<SessionId> {
        reset()

        return authenticationService.loginWithQrCode(qrCodeLoginData) { step ->
            _currentLoginStep.value = step
        }.onFailure { throwable ->
            if (throwable is QrLoginException) {
                _currentLoginStep.value = QrCodeLoginStep.Failed(throwable)
            }
        }
    }

    override fun reset() {
        _currentLoginStep.value = QrCodeLoginStep.Uninitialized
    }
}
