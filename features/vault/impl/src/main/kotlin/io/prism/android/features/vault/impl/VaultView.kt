/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import io.prism.android.libraries.designsystem.theme.components.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultView(
    state: VaultState,
    onBack: () -> Unit,
    onRoomClick: (roomId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(CommonStrings.screen_vault_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.LockOpen, contentDescription = stringResource(CommonStrings.action_back))
                    }
                },
                actions = {
                    if (!state.isLocked) {
                        IconButton(onClick = { state.eventSink(VaultEvent.LockVault) }) {
                            Icon(Icons.Default.Lock, contentDescription = stringResource(CommonStrings.a11y_lock))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLocked) {
                LockedContent(
                    isBiometricAvailable = state.isBiometricAvailable,
                    onUnlock = { state.eventSink(VaultEvent.UnlockVault) },
                )
            } else {
                UnlockedContent(
                    roomIds = state.vaultRoomIds,
                    onRoomClick = onRoomClick,
                    onRemove = { roomId -> state.eventSink(VaultEvent.RemoveFromVault(roomId)) },
                )
            }
        }
    }

    state.biometricError?.let { error ->
        AlertDialog(
            onDismissRequest = { state.eventSink(VaultEvent.DismissBiometricError) },
            title = { Text(stringResource(CommonStrings.screen_vault_authentication_failed)) },
            text = { Text(error) },
            confirmButton = {
                TextButton(
                    text = stringResource(CommonStrings.action_ok),
                    onClick = { state.eventSink(VaultEvent.DismissBiometricError) },
                )
            }
        )
    }
}

@Composable
private fun LockedContent(
    isBiometricAvailable: Boolean,
    onUnlock: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(CommonStrings.screen_vault_locked_title))
        Spacer(modifier = Modifier.height(24.dp))
        if (isBiometricAvailable) {
            Button(
                text = stringResource(CommonStrings.screen_vault_unlock_biometrics),
                onClick = onUnlock,
            )
        } else {
            Text(stringResource(CommonStrings.screen_vault_biometric_unavailable))
        }
    }
}

@Composable
private fun UnlockedContent(
    roomIds: List<String>,
    onRoomClick: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    if (roomIds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(CommonStrings.screen_vault_empty_state))
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(roomIds) { roomId ->
            ListItem(
                headlineContent = { Text(roomId) },
                trailingContent = ListItemContent.Custom { _ ->
                    TextButton(
                        text = stringResource(CommonStrings.screen_vault_remove),
                        onClick = { onRemove(roomId) },
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
    }
}
