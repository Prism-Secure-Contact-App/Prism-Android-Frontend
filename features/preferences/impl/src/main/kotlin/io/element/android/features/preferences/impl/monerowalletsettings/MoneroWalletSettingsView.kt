/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.components.preferences.PreferencePage
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.TextButton
import io.prism.android.libraries.designsystem.theme.components.TextField
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarHost

@Composable
fun MoneroWalletSettingsView(
    state: MoneroWalletSettingsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            state.eventSink(MoneroWalletSettingsEvents.DismissSnackbar)
        }
    }

    if (state.showWithdrawDialog) {
        val isLoading = state.withdrawAction is AsyncAction.Loading
        val error = (state.withdrawAction as? AsyncAction.Failure)?.error?.message
        AlertDialog(
            onDismissRequest = { state.eventSink(MoneroWalletSettingsEvents.DismissWithdrawDialog) },
            title = { Text("Withdraw") },
            text = {
                Column {
                    Text("Send your XMR to an external address.")
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = state.withdrawAddress,
                        onValueChange = { state.eventSink(MoneroWalletSettingsEvents.SetWithdrawAddress(it)) },
                        label = "Recipient Address",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = state.withdrawAmount,
                        onValueChange = { state.eventSink(MoneroWalletSettingsEvents.SetWithdrawAmount(it)) },
                        label = "Amount (XMR)",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Estimated network fee: ${state.feeRate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    text = if (isLoading) "Sending..." else "Send",
                    onClick = { state.eventSink(MoneroWalletSettingsEvents.SubmitWithdraw) },
                    enabled = !isLoading && state.withdrawAddress.isNotBlank() && state.withdrawAmount.isNotBlank(),
                )
            },
            dismissButton = {
                TextButton(
                    text = "Cancel",
                    onClick = { state.eventSink(MoneroWalletSettingsEvents.DismissWithdrawDialog) },
                )
            }
        )
    }

    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = "Wallet Security",
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Address
        ListItem(
            headlineContent = { Text("Monero Adresi (XMR)") },
            supportingContent = {
                Text(
                    text = state.address ?: "Loading...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyAddress) },
        )

        HorizontalDivider()

        // View Key
        ListItem(
            headlineContent = { Text("View Key") },
            supportingContent = {
                Text(
                    text = state.viewKey ?: "Yükleniyor...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyViewKey) },
        )

        HorizontalDivider()

        // Spend Key
        ListItem(
            headlineContent = { Text("Spend Key") },
            supportingContent = {
                Text(
                    text = state.spendKey ?: "Yükleniyor...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopySpendKey) },
        )

        HorizontalDivider()

        // Balance
        ListItem(
            headlineContent = { Text("Balance") },
            supportingContent = { Text(state.balance) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Chart())),
        )

        HorizontalDivider()

        // Deposit / Withdraw actions
        ListItem(
            headlineContent = { Text("Deposit") },
            supportingContent = { Text("Send XMR to your Monero address") },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ArrowDown())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyAddress) },
        )
        ListItem(
            headlineContent = { Text("Withdraw") },
            supportingContent = { Text("Send your XMR to an external address") },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ArrowUp())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.ShowWithdrawDialog) },
        )

        HorizontalDivider()

        // Seed Phrase
        if (state.isRevealed) {
            ListItem(
                headlineContent = { Text("Recovery Phrase (Seed)") },
                supportingContent = {
                    Text(
                        text = state.mnemonic ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    )
                },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Document())),
                onClick = { state.eventSink(MoneroWalletSettingsEvents.CopySeedPhrase) },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "⚠️ Bu 12 kelimeyi güvenli bir yere yazın. Cüzdanınıza erişmek için tek yoldur. " +
                    "Kimseyle paylaşmayın; PRISM sunucularında saklanmaz (non-custodial).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        } else {
            ListItem(
                headlineContent = { Text("Show Recovery Phrase") },
                supportingContent = { Text("Hidden. Tap to reveal.") },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
                onClick = { state.eventSink(MoneroWalletSettingsEvents.RevealSeedPhrase) },
            )
        }
    }
}
