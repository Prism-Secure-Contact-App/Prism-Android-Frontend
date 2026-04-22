/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.monero

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.prism.android.features.messages.impl.R
import io.prism.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.prism.android.libraries.designsystem.theme.components.OutlinedTextField
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun MoneroTransferDialog(
    onDismiss: () -> Unit,
    onSendClick: (amount: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amount by remember { mutableStateOf("") }
    val amountAsDouble = amount.toDoubleOrNull() ?: 0.0
    val isAmountValid = amountAsDouble > 0.0

    ConfirmationDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        submitText = stringResource(CommonStrings.action_send),
        onSubmitClick = { if (isAmountValid) onSendClick(amountAsDouble) },
        cancelText = stringResource(CommonStrings.action_cancel),
        onCancelClick = onDismiss,
        title = "Send XMR",
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter XMR amount to transfer. A 0.5% platform fee will be applied automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (XMR)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                if (isAmountValid) {
                    val platformFee = (amountAsDouble * 0.005).coerceAtLeast(0.001).coerceAtMost(0.05)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimated Fee: $platformFee XMR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
