/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.root

import io.prism.android.features.logout.api.direct.DirectLogoutState
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.matrix.api.core.DeviceId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class PreferencesRootState(
    val myUser: PRISMUser,
    val version: String,
    val deviceId: DeviceId?,
    val isMultiAccountEnabled: Boolean,
    val otherSessions: ImmutableList<PRISMUser>,
    val showSecureBackup: Boolean,
    val showSecureBackupBadge: Boolean,
    val accountManagementUrl: String?,
    val devicesManagementUrl: String?,
    val canReportBug: Boolean,
    val showLinkNewDevice: Boolean,
    val showAnalyticsSettings: Boolean,
    val showDeveloperSettings: Boolean,
    val canDeactivateAccount: Boolean,
    val showBlockedUsersItem: Boolean,
    val showLabsItem: Boolean,
    val directLogoutState: DirectLogoutState,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (PreferencesRootEvents) -> Unit,
)
