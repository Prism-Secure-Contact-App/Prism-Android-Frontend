/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.root

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.preferences.impl.R
import io.prism.android.features.preferences.impl.user.UserPreferences
import io.prism.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.components.preferences.PreferencePage
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PRISMPreviewDark
import io.prism.android.libraries.designsystem.preview.PRISMPreviewLight
import io.prism.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.ListItemStyle
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.prism.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.prism.android.libraries.matrix.api.core.DeviceId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.components.MatrixUserProvider
import io.prism.android.libraries.matrix.ui.components.MatrixUserRow
import io.prism.android.libraries.matrix.ui.components.aMatrixUserList
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun PreferencesRootView(
    state: PreferencesRootState,
    onBackClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onSecureBackupClick: () -> Unit,
    onManageAccountClick: (url: String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenRageShake: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenLabs: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenUserProfile: (PRISMUser) -> Unit,
    onOpenBlockedUsers: () -> Unit,
    onOpenBridgeSettings: () -> Unit,
    onOpenMoneroWalletSettings: () -> Unit,
    onOpenLlmApiSettings: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)

    // Include pref from other modules
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = CommonStrings.common_settings),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        UserPreferences(
            modifier = Modifier.clickable {
                onOpenUserProfile(state.myUser)
            },
            user = state.myUser,
        )
        if (state.isMultiAccountEnabled) {
            MultiAccountSection(
                state = state,
                onAddAccountClick = onAddAccountClick,
            )
        }
        // 'Manage my app' section
        ManageAppSection(
            state = state,
            onOpenNotificationSettings = onOpenNotificationSettings,
            onOpenLockScreenSettings = onOpenLockScreenSettings,
            onSecureBackupClick = onSecureBackupClick,
        )

        // 'Account' section
        ManageAccountSection(
            state = state,
            onManageAccountClick = onManageAccountClick,
            onLinkNewDeviceClick = onLinkNewDeviceClick,
            onOpenBlockedUsers = onOpenBlockedUsers,
            onOpenBridgeSettings = onOpenBridgeSettings,
        )

        // General section
        GeneralSection(
            state = state,
            onOpenAbout = onOpenAbout,
            onOpenAnalytics = onOpenAnalytics,
            onOpenRageShake = onOpenRageShake,
            onOpenAdvancedSettings = onOpenAdvancedSettings,
            onOpenLabs = onOpenLabs,
            onOpenMoneroWalletSettings = onOpenMoneroWalletSettings,
            onOpenLlmApiSettings = onOpenLlmApiSettings,
            onSignOutClick = onSignOutClick,
            onDeactivateClick = onDeactivateClick,
        )

        Footer(
            version = state.version,
            deviceId = state.deviceId,
        )
    }
}

@Composable
private fun ColumnScope.MultiAccountSection(
    state: PreferencesRootState,
    onAddAccountClick: () -> Unit,
) {
    HorizontalDivider(
        thickness = 8.dp,
        color = PRISMTheme.colors.bgSubtleSecondary,
    )
    state.otherSessions.forEach { matrixUser ->
        MatrixUserRow(
            modifier = Modifier.clickable {
                state.eventSink(PreferencesRootEvents.SwitchToSession(matrixUser.userId))
            },
            matrixUser = matrixUser,
            avatarSize = AvatarSize.AccountItem,
        )
        HorizontalDivider()
    }
    ListItem(
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus())),
        headlineContent = {
            Text(stringResource(CommonStrings.common_add_another_account))
        },
        onClick = onAddAccountClick,
    )
    HorizontalDivider(
        thickness = 8.dp,
        color = PRISMTheme.colors.bgSubtleSecondary,
    )
}

@Composable
private fun ColumnScope.ManageAppSection(
    state: PreferencesRootState,
    onOpenNotificationSettings: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onSecureBackupClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(stringResource(id = R.string.screen_notification_settings_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Notifications())),
        onClick = onOpenNotificationSettings,
    )
    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.common_screen_lock)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
        onClick = onOpenLockScreenSettings,
    )
    if (state.showSecureBackup) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_encryption)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            trailingContent = ListItemContent.Badge.takeIf { state.showSecureBackupBadge },
            onClick = onSecureBackupClick,
        )
    }
    HorizontalDivider()
}

@Composable
private fun ColumnScope.ManageAccountSection(
    state: PreferencesRootState,
    onManageAccountClick: (url: String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onOpenBlockedUsers: () -> Unit,
    onOpenBridgeSettings: () -> Unit,
) {
    if (state.showLinkNewDevice) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_link_new_device)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Devices())),
            onClick = onLinkNewDeviceClick,
        )
    }
    state.accountManagementUrl?.let { url ->
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_account)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserProfile())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.PopOut())),
            onClick = { onManageAccountClick(url) },
        )
    }

    state.devicesManagementUrl?.let { url ->
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_devices)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Devices())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.PopOut())),
            onClick = { onManageAccountClick(url) },
        )
    }

    if (state.showBlockedUsersItem) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_blocked_users)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Block())),
            onClick = onOpenBlockedUsers,
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(id = R.string.screen_bridge_settings_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Link())),
        onClick = onOpenBridgeSettings,
    )

    if (state.accountManagementUrl != null || state.devicesManagementUrl != null || state.showBlockedUsersItem || true) {
        HorizontalDivider()
    }
}

@Composable
private fun ColumnScope.GeneralSection(
    state: PreferencesRootState,
    onOpenAbout: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenRageShake: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenLabs: () -> Unit,
    onOpenMoneroWalletSettings: () -> Unit,
    onOpenLlmApiSettings: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeactivateClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.common_about)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Info())),
        onClick = onOpenAbout,
    )
    if (state.canReportBug) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_report_a_problem)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChatProblem())),
            onClick = onOpenRageShake
        )
    }
    if (state.showAnalyticsSettings) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_analytics)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Chart())),
            onClick = onOpenAnalytics,
        )
    }
    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.common_advanced_settings)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Settings())),
        onClick = onOpenAdvancedSettings,
    )

    ListItem(
        headlineContent = { Text(stringResource(id = R.string.screen_monero_wallet_settings_title)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
        onClick = onOpenMoneroWalletSettings,
    )

    ListItem(
        headlineContent = { Text(stringResource(id = R.string.screen_preferences_prism_ai_api_keys)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Labs())),
        onClick = onOpenLlmApiSettings,
    )

    ListItem(
        headlineContent = { Text(stringResource(id = R.string.screen_preferences_deep_work_mode)) },
        supportingContent = { Text(if (state.isDeepWorkModeEnabled) stringResource(id = R.string.screen_preferences_deep_work_mode_active) else stringResource(id = R.string.screen_preferences_deep_work_mode_inactive)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Computer())),
        trailingContent = ListItemContent.Custom {
            androidx.compose.material3.Switch(
                checked = state.isDeepWorkModeEnabled,
                onCheckedChange = { state.eventSink(PreferencesRootEvents.ToggleDeepWorkMode) },
            )
        },
    )

    if (state.showLabsItem) {
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.screen_labs_title)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Labs())),
            onClick = onOpenLabs,
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.action_signout)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.SignOut())),
        style = ListItemStyle.Destructive,
        onClick = onSignOutClick,
    )
    if (state.canDeactivateAccount) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_deactivate_account)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Warning())),
            style = ListItemStyle.Destructive,
            onClick = onDeactivateClick,
        )
    }

}

@Composable
private fun ColumnScope.Footer(
    version: String,
    deviceId: DeviceId?,
) {
    val text = remember(version, deviceId) {
        buildString {
            append(version)
            if (deviceId != null) {
                append("\n")
                append(deviceId)
            }
        }
    }
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 16.dp)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
        textAlign = TextAlign.Center,
        text = text,
        style = PRISMTheme.typography.fontBodySmRegular,
        color = PRISMTheme.colors.textSecondary,
    )
}

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewLightPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: PRISMUser) =
    PRISMPreviewLight { ContentToPreview(matrixUser) }

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewDarkPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: PRISMUser) =
    PRISMPreviewDark { ContentToPreview(matrixUser) }

@ExcludeFromCoverage
@Composable
private fun ContentToPreview(matrixUser: PRISMUser) {
    PreferencesRootView(
        state = aPreferencesRootState(myUser = matrixUser),
        onBackClick = {},
        onAddAccountClick = {},
        onOpenAnalytics = {},
        onOpenRageShake = {},
        onOpenAdvancedSettings = {},
        onOpenLabs = {},
        onOpenAbout = {},
        onSecureBackupClick = {},
        onManageAccountClick = {},
        onLinkNewDeviceClick = {},
        onOpenNotificationSettings = {},
        onOpenLockScreenSettings = {},
        onOpenUserProfile = {},
        onOpenBlockedUsers = {},
        onOpenBridgeSettings = {},
        onOpenMoneroWalletSettings = {},
        onOpenLlmApiSettings = {},
        onSignOutClick = {},
        onDeactivateClick = {},
    )
}

@PreviewsDayNight
@Composable
internal fun MultiAccountSectionPreview() = PRISMPreview {
    Column {
        MultiAccountSection(
            state = aPreferencesRootState(
                otherSessions = aMatrixUserList(),
            ),
            onAddAccountClick = {},
        )
    }
}
