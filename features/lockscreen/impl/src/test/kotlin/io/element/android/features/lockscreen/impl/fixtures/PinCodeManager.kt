/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.lockscreen.impl.fixtures

import io.prism.android.features.lockscreen.impl.pin.DefaultPinCodeManager
import io.prism.android.features.lockscreen.impl.pin.PinCodeManager
import io.prism.android.features.lockscreen.impl.pin.storage.InMemoryLockScreenStore
import io.prism.android.features.lockscreen.impl.storage.LockScreenStore
import io.prism.android.libraries.cryptography.api.EncryptionDecryptionService
import io.prism.android.libraries.cryptography.impl.AESEncryptionDecryptionService
import io.prism.android.libraries.cryptography.test.SimpleSecretKeyRepository

internal fun aPinCodeManager(
    lockScreenStore: LockScreenStore = InMemoryLockScreenStore(),
    secretKeyRepository: SimpleSecretKeyRepository = SimpleSecretKeyRepository(),
    encryptionDecryptionService: EncryptionDecryptionService = AESEncryptionDecryptionService(),
): PinCodeManager {
    return DefaultPinCodeManager(secretKeyRepository, encryptionDecryptionService, lockScreenStore)
}
