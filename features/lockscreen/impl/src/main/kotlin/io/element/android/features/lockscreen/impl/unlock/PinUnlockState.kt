/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.lockscreen.impl.unlock

import io.prism.android.features.lockscreen.impl.biometric.BiometricAuthenticator
import io.prism.android.features.lockscreen.impl.biometric.BiometricUnlockError
import io.prism.android.features.lockscreen.impl.pin.model.PinEntry
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData

data class PinUnlockState(
    val pinEntry: AsyncData<PinEntry>,
    val showWrongPinTitle: Boolean,
    val remainingAttempts: AsyncData<Int>,
    val showSignOutPrompt: Boolean,
    val signOutAction: AsyncAction<Unit>,
    val showBiometricUnlock: Boolean,
    val isUnlocked: Boolean,
    val biometricUnlockResult: BiometricAuthenticator.AuthenticationResult?,
    val eventSink: (PinUnlockEvents) -> Unit
) {
    val isSignOutPromptCancellable = when (remainingAttempts) {
        is AsyncData.Success -> remainingAttempts.data > 0
        else -> true
    }

    val biometricUnlockErrorMessage = when {
        biometricUnlockResult is BiometricAuthenticator.AuthenticationResult.Failure &&
            biometricUnlockResult.error is BiometricUnlockError &&
            biometricUnlockResult.error.isAuthDisabledError -> {
            biometricUnlockResult.error.message
        }
        else -> null
    }
    val showBiometricUnlockError = biometricUnlockErrorMessage != null
}
