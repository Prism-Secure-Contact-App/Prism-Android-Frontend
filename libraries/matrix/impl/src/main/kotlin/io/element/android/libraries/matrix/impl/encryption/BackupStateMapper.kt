/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.encryption

import io.prism.android.libraries.matrix.api.encryption.BackupState
import org.matrix.rustcomponents.sdk.BackupState as RustBackupState

class BackupStateMapper {
    fun map(backupState: RustBackupState): BackupState {
        return when (backupState) {
            RustBackupState.UNKNOWN -> BackupState.UNKNOWN
            RustBackupState.CREATING -> BackupState.CREATING
            RustBackupState.ENABLING -> BackupState.ENABLING
            RustBackupState.RESUMING -> BackupState.RESUMING
            RustBackupState.ENABLED -> BackupState.ENABLED
            RustBackupState.DOWNLOADING -> BackupState.DOWNLOADING
            RustBackupState.DISABLING -> BackupState.DISABLING
        }
    }
}
