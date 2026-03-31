/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

sealed interface VaultEvent {
    /** Kullanıcı biyometrik doğrulama istiyor (kasayı açmak için). */
    data object UnlockVault : VaultEvent

    /** Kasayı tekrar kilitle. */
    data object LockVault : VaultEvent

    /** Odayı kasadan çıkar. */
    data class RemoveFromVault(val roomId: String) : VaultEvent

    /** Biyometrik hata mesajını kapat. */
    data object DismissBiometricError : VaultEvent
}
