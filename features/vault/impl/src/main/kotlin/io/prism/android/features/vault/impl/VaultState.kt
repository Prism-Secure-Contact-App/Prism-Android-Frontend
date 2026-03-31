/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class VaultState(
    /** Kasa kilitli mi? Kilitliyse biyometrik istek gösterilir. */
    val isLocked: Boolean = true,
    /** Kasadaki room ID listesi. Kilitli iken boş gösterilir. */
    val vaultRoomIds: ImmutableList<String> = persistentListOf(),
    /** Biyometrik doğrulama destekleniyor mu? */
    val isBiometricAvailable: Boolean = false,
    /** Biyometrik hata mesajı */
    val biometricError: String? = null,
    val eventSink: (VaultEvent) -> Unit,
)
