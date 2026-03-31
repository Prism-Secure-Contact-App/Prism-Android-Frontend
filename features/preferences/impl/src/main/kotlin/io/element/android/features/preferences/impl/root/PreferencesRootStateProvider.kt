/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.root

import io.prism.android.features.logout.api.direct.aDirectLogoutState
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.prism.api.core.DeviceId
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

fun aPreferencesRootState(
    myUser: PRISMUser = aPRISMUser(),
    otherSessions: List<PRISMUser> = emptyList(),
    eventSink: (PreferencesRootEvents) -> Unit = { _ -> },
) = PreferencesRootState(
    myUser = myUser,
    version = "Version 1.1 (1)",
    deviceId = DeviceId("ILAKNDNASDLK"),
    isMultiAccountEnabled = true,
    otherSessions = otherSessions.toImmutableList(),
    showSecureBackup = true,
    showSecureBackupBadge = true,
    accountManagementUrl = "aUrl",
    devicesManagementUrl = "anOtherUrl",
    showAnalyticsSettings = true,
    showLinkNewDevice = true,
    canReportBug = true,
    showDeveloperSettings = true,
    showBlockedUsersItem = true,
    showLabsItem = true,
    canDeactivateAccount = true,
    snackbarMessage = SnackbarMessage(CommonStrings.common_verification_complete),
    directLogoutState = aDirectLogoutState(),
    eventSink = eventSink,
)
