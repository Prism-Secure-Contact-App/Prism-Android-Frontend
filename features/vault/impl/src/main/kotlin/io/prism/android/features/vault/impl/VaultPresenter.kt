/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.architecture.Presenter
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import uk.fathertkt.prism.vault.VaultManager

@Inject
class VaultPresenter(
    private val vaultManager: VaultManager,
) : Presenter<VaultState> {

    @Composable
    override fun present(): VaultState {
        val activity = LocalActivity.current as? FragmentActivity
        val coroutineScope = rememberCoroutineScope()

        val vaultRoomIds by vaultManager.vaultRooms.collectAsState(initial = emptySet())
        val isBiometricAvailable = remember { vaultManager.isBiometricAvailable() }

        var isLocked by remember { mutableStateOf(true) }
        var biometricError by remember { mutableStateOf<String?>(null) }

        fun handleEvent(event: VaultEvent) {
            when (event) {
                VaultEvent.UnlockVault -> {
                    val currentActivity = activity ?: return
                    vaultManager.promptBiometric(
                        activity = currentActivity,
                        onSuccess = { isLocked = false },
                        onFailure = { error -> biometricError = error },
                    )
                }
                VaultEvent.LockVault -> isLocked = true
                is VaultEvent.RemoveFromVault -> {
                    coroutineScope.launch { vaultManager.removeFromVault(event.roomId) }
                }
                VaultEvent.DismissBiometricError -> biometricError = null
            }
        }

        return VaultState(
            isLocked = isLocked,
            vaultRoomIds = if (isLocked) persistentListOf() else vaultRoomIds.toImmutableList(),
            isBiometricAvailable = isBiometricAvailable,
            biometricError = biometricError,
            eventSink = ::handleEvent,
        )
    }
}
