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
import androidx.compose.ui.res.stringResource
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.preferences.impl.R
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
            title = { Text(stringResource(R.string.screen_monero_wallet_settings_withdraw_dialog_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.screen_monero_wallet_settings_withdraw_dialog_description))
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = state.withdrawAddress,
                        onValueChange = { state.eventSink(MoneroWalletSettingsEvents.SetWithdrawAddress(it)) },
                        label = stringResource(R.string.screen_monero_wallet_settings_withdraw_address_label),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = state.withdrawAmount,
                        onValueChange = { state.eventSink(MoneroWalletSettingsEvents.SetWithdrawAmount(it)) },
                        label = stringResource(R.string.screen_monero_wallet_settings_withdraw_amount_label),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.screen_monero_wallet_settings_withdraw_fee, state.feeRate),
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
                    text = if (isLoading) stringResource(R.string.screen_monero_wallet_settings_withdraw_sending) else stringResource(R.string.screen_monero_wallet_settings_withdraw_send),
                    onClick = { state.eventSink(MoneroWalletSettingsEvents.SubmitWithdraw) },
                    enabled = !isLoading && state.withdrawAddress.isNotBlank() && state.withdrawAmount.isNotBlank(),
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(R.string.screen_monero_wallet_settings_withdraw_cancel),
                    onClick = { state.eventSink(MoneroWalletSettingsEvents.DismissWithdrawDialog) },
                )
            }
        )
    }

    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(R.string.screen_monero_wallet_settings_title),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Address
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_address_label)) },
            supportingContent = {
                Text(
                    text = state.address ?: stringResource(R.string.screen_monero_wallet_settings_loading),
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyAddress) },
        )

        HorizontalDivider()

        // View Key
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_view_key_label)) },
            supportingContent = {
                Text(
                    text = state.viewKey ?: stringResource(R.string.screen_monero_wallet_settings_loading),
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyViewKey) },
        )

        HorizontalDivider()

        // Spend Key
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_spend_key_label)) },
            supportingContent = {
                Text(
                    text = state.spendKey ?: stringResource(R.string.screen_monero_wallet_settings_loading),
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                )
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopySpendKey) },
        )

        HorizontalDivider()

        // Balance
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_balance_label)) },
            supportingContent = { Text(state.balance) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Chart())),
        )

        HorizontalDivider()

        // Deposit / Withdraw actions
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_deposit_label)) },
            supportingContent = { Text(stringResource(R.string.screen_monero_wallet_settings_deposit_description)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ArrowDown())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.CopyAddress) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_withdraw_label)) },
            supportingContent = { Text(stringResource(R.string.screen_monero_wallet_settings_withdraw_description)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ArrowUp())),
            onClick = { state.eventSink(MoneroWalletSettingsEvents.ShowWithdrawDialog) },
        )

        HorizontalDivider()

        // Seed Phrase
        if (state.isRevealed) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_recovery_phrase_label)) },
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
                text = stringResource(R.string.screen_monero_wallet_settings_recovery_phrase_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        } else {
            ListItem(
                headlineContent = { Text(stringResource(R.string.screen_monero_wallet_settings_show_recovery_phrase)) },
                supportingContent = { Text(stringResource(R.string.screen_monero_wallet_settings_recovery_phrase_hidden)) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
                onClick = { state.eventSink(MoneroWalletSettingsEvents.RevealSeedPhrase) },
            )
        }
    }
}
