/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.bridgesettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.preferences.impl.R
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.components.preferences.PreferencePage
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.ListItemStyle
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarHost

@Composable
fun BridgeSettingsView(
    state: BridgeSettingsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            state.eventSink(BridgeSettingsEvents.DismissSnackbar)
        }
    }

    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = R.string.screen_bridge_settings_title),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Active bridges
        state.bridges.filter { it.isActive }.forEach { bridge ->
            ListItem(
                headlineContent = { Text(bridge.displayName) },
                supportingContent = {
                    val id = bridge.identifier
                    val status = when {
                        id != null -> "${stringResource(R.string.screen_bridge_settings_status_connected)} · $id"
                        else -> stringResource(R.string.screen_bridge_settings_status_connected)
                    }
                    Text(status)
                },
                leadingContent = ListItemContent.Icon(
                    IconSource.Vector(
                        when (bridge.platform) {
                            "whatsapp" -> CompoundIcons.Chat()
                            else -> CompoundIcons.Image()
                        }
                    )
                ),
                trailingContent = if (state.isLoading) {
                    ListItemContent.Custom { CircularProgressIndicator(Modifier.size(24.dp)) }
                } else {
                    ListItemContent.Text(stringResource(R.string.screen_bridge_settings_action_remove))
                },
                style = ListItemStyle.Destructive,
                onClick = {
                    if (!state.isLoading) {
                        state.eventSink(BridgeSettingsEvents.DisconnectBridge(bridge.platform))
                    }
                }
            )
        }

        if (state.bridges.any { it.isActive }) {
            HorizontalDivider()
        }

        // Available / not-yet-connected platforms
        state.bridges.filter { !it.isActive }.forEach { bridge ->
            ListItem(
                headlineContent = { Text(bridge.displayName) },
                supportingContent = { Text(stringResource(R.string.screen_bridge_settings_status_disconnected)) },
                leadingContent = ListItemContent.Icon(
                    IconSource.Vector(
                        when (bridge.platform) {
                            "whatsapp" -> CompoundIcons.Chat()
                            else -> CompoundIcons.Image()
                        }
                    )
                ),
                trailingContent = if (state.isLoading) {
                    ListItemContent.Custom { CircularProgressIndicator(Modifier.size(24.dp)) }
                } else {
                    ListItemContent.Text(stringResource(R.string.screen_bridge_settings_action_add))
                },
                onClick = {
                    if (!state.isLoading) {
                        state.eventSink(BridgeSettingsEvents.ConnectBridge(bridge.platform))
                    }
                }
            )
        }

        // Allow reconnecting / changing number (auto-logout existing session first)
        val whatsappBridge = state.bridges.find { it.platform == "whatsapp" }
        val isWhatsappConnected = whatsappBridge?.isActive == true
        HorizontalDivider()
        ListItem(
            headlineContent = {
                Text(if (isWhatsappConnected) "Change WhatsApp Number" else "Connect WhatsApp")
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus())),
            onClick = {
                if (!state.isLoading) {
                    state.eventSink(BridgeSettingsEvents.ConnectBridge("whatsapp"))
                }
            }
        )
    }

    // Dialogs
    when (val d = state.dialog) {
        is BridgeDialog.PhoneInput -> {
            var phone by remember(d) { mutableStateOf(d.initialValue) }
            AlertDialog(
                onDismissRequest = { state.eventSink(BridgeSettingsEvents.DismissDialog) },
                title = { Text("Phone Number") },
                text = {
                    Column {
                        Text(d.prompt)
                        Spacer(Modifier.height(8.dp))
                        TextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(d.inputLabel) },
                            placeholder = { Text(d.placeholder) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { state.eventSink(BridgeSettingsEvents.SubmitPhone(phone)) },
                        enabled = phone.isNotBlank() && phone != "+"
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { state.eventSink(BridgeSettingsEvents.DismissDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }
        is BridgeDialog.PairingCode -> {
            AlertDialog(
                onDismissRequest = { state.eventSink(BridgeSettingsEvents.DismissDialog) },
                title = { Text("Pairing Code") },
                text = {
                    Column {
                        Text(d.caption)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = d.code,
                            modifier = Modifier.fillMaxWidth(),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { state.eventSink(BridgeSettingsEvents.DismissDialog) }) {
                        Text("Close")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { state.eventSink(BridgeSettingsEvents.RequestNewCode) }) {
                        Text("Get new code")
                    }
                }
            )
        }
        is BridgeDialog.Error -> {
            AlertDialog(
                onDismissRequest = { state.eventSink(BridgeSettingsEvents.DismissDialog) },
                title = { Text("Error") },
                text = { Text(d.message) },
                confirmButton = {
                    TextButton(onClick = { state.eventSink(BridgeSettingsEvents.DismissDialog) }) {
                        Text("OK")
                    }
                }
            )
        }
        null -> Unit
    }
}
