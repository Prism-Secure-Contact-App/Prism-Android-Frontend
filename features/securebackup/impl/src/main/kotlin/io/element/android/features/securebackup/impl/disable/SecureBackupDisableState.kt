/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securebackup.impl.disable

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.encryption.BackupState

data class SecureBackupDisableState(
    val backupState: BackupState,
    val disableAction: AsyncAction<Unit>,
    val appName: String,
    val eventSink: (SecureBackupDisableEvents) -> Unit
)
