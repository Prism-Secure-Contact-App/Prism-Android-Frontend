/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securebackup.impl.disable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.features.securebackup.impl.loggerTagDisable
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.architecture.runCatchingUpdatingState
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.matrix.api.encryption.EncryptionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Inject
class SecureBackupDisablePresenter(
    private val encryptionService: EncryptionService,
    private val buildMeta: BuildMeta,
) : Presenter<SecureBackupDisableState> {
    @Composable
    override fun present(): SecureBackupDisableState {
        val backupState by encryptionService.backupStateStateFlow.collectAsState()
        Timber.tag(loggerTagDisable.value).d("backupState: $backupState")
        val disableAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val coroutineScope = rememberCoroutineScope()
        fun handleEvent(event: SecureBackupDisableEvents) {
            when (event) {
                is SecureBackupDisableEvents.DisableBackup -> coroutineScope.disableBackup(disableAction)
                SecureBackupDisableEvents.DismissDialogs -> {
                    disableAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return SecureBackupDisableState(
            backupState = backupState,
            disableAction = disableAction.value,
            appName = buildMeta.applicationName,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.disableBackup(disableAction: MutableState<AsyncAction<Unit>>) = launch {
        suspend {
            Timber.tag(loggerTagDisable.value).d("Calling encryptionService.disableRecovery()")
            encryptionService.disableRecovery().getOrThrow()
        }.runCatchingUpdatingState(disableAction)
    }
}
