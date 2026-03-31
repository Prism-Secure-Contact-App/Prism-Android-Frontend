/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securebackup.impl.disable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.securebackup.impl.R
import io.prism.android.libraries.designsystem.atomic.pages.FlowStepPage
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.components.async.AsyncActionView
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
fun SecureBackupDisableView(
    state: SecureBackupDisableState,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowStepPage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = R.string.screen_key_backup_disable_title),
        subTitle = stringResource(id = R.string.screen_key_backup_disable_description),
        iconStyle = BigIcon.Style.AlertSolid,
        buttons = { Buttons(state = state) },
    ) {
        Content(state = state)
    }

    AsyncActionView(
        async = state.disableAction,
        progressDialog = {},
        errorMessage = { it.message ?: it.toString() },
        onErrorDismiss = { state.eventSink.invoke(SecureBackupDisableEvents.DismissDialogs) },
        onSuccess = { onSuccess() },
    )
}

@Composable
private fun ColumnScope.Buttons(
    state: SecureBackupDisableState,
) {
    Button(
        text = stringResource(id = R.string.screen_chat_backup_key_backup_action_disable),
        showProgress = state.disableAction.isLoading(),
        destructive = true,
        modifier = Modifier.fillMaxWidth(),
        onClick = { state.eventSink.invoke(SecureBackupDisableEvents.DisableBackup) }
    )
}

@Composable
private fun Content(state: SecureBackupDisableState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SecureBackupDisableItem(stringResource(id = R.string.screen_key_backup_disable_description_point_1))
        SecureBackupDisableItem(stringResource(id = R.string.screen_key_backup_disable_description_point_2, state.appName))
    }
}

@Composable
private fun SecureBackupDisableItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PRISMTheme.colors.bgActionSecondaryHovered)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = CompoundIcons.Close(),
            contentDescription = null,
            tint = PRISMTheme.colors.iconCriticalPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            color = PRISMTheme.colors.textSecondary,
            style = PRISMTheme.typography.fontBodyMdRegular,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SecureBackupDisableViewPreview(
    @PreviewParameter(SecureBackupDisableStateProvider::class) state: SecureBackupDisableState
) = PRISMPreview {
    SecureBackupDisableView(
        state = state,
        onSuccess = {},
        onBackClick = {},
    )
}
