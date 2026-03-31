/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.encryption.BackupState
import io.prism.android.libraries.prism.api.encryption.BackupUploadState
import io.prism.android.libraries.prism.api.encryption.RecoveryState

data class LogoutState(
    val isLastDevice: Boolean,
    val backupState: BackupState,
    val doesBackupExistOnServer: Boolean,
    val recoveryState: RecoveryState,
    val backupUploadState: BackupUploadState,
    val waitingForALongTime: Boolean,
    val logoutAction: AsyncAction<Unit>,
    val eventSink: (LogoutEvents) -> Unit,
)
