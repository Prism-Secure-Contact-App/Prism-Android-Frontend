/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.prism.android.libraries.designsystem.theme.components.Button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightningView(
    state: LightningState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Lightning Cüzdan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            BalanceCard(
                isConnected = state.isConnected,
                balanceSats = state.balanceSats,
            )
            Spacer(Modifier.height(16.dp))
            ActionButtons(state = state)
            Spacer(Modifier.height(16.dp))
            Text("İşlem Geçmişi", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            PaymentList(payments = state.payments)
        }
    }

    // Oluşturulan fatura dialogu
    state.generatedInvoice?.let { bolt11 ->
        AlertDialog(
            onDismissRequest = { state.eventSink(LightningEvent.DismissInvoice) },
            title = { Text("Fatura Hazır") },
            text = {
                Column {
                    Text("Bu kodu karşı tarafa gönderin:")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = bolt11,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Mono,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { state.eventSink(LightningEvent.DismissInvoice) }) {
                    Text("Tamam")
                }
            }
        )
    }

    // Hata dialogu
    state.error?.let { err ->
        AlertDialog(
            onDismissRequest = { state.eventSink(LightningEvent.DismissError) },
            title = { Text("Hata") },
            text = { Text(err) },
            confirmButton = {
                TextButton(onClick = { state.eventSink(LightningEvent.DismissError) }) {
                    Text("Tamam")
                }
            }
        )
    }
}

@Composable
private fun BalanceCard(isConnected: Boolean, balanceSats: Long) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isConnected) "Bağlı" else "Bağlantı yok",
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$balanceSats sat",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "≈ ${balanceSats / 100_000_000.0} BTC",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ActionButtons(state: LightningState) {
    var showSendDialog by remember { mutableStateOf(false) }
    var showReceiveDialog by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { showSendDialog = true },
            modifier = Modifier.weight(1f),
            enabled = state.isConnected,
        ) { Text("Gönder") }
        Button(
            onClick = { showReceiveDialog = true },
            modifier = Modifier.weight(1f),
            enabled = state.isConnected,
        ) { Text("Al") }
    }

    if (showSendDialog) {
        SendPaymentDialog(
            onDismiss = { showSendDialog = false },
            onPay = { bolt11 ->
                state.eventSink(LightningEvent.PayInvoice(bolt11))
                showSendDialog = false
            }
        )
    }

    if (showReceiveDialog) {
        CreateInvoiceDialog(
            isLoading = state.isCreatingInvoice,
            onDismiss = { showReceiveDialog = false },
            onCreate = { amount, desc ->
                state.eventSink(LightningEvent.CreateInvoice(amount, desc))
                showReceiveDialog = false
            }
        )
    }
}

@Composable
private fun SendPaymentDialog(onDismiss: () -> Unit, onPay: (String) -> Unit) {
    var bolt11 by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fatura Öde") },
        text = {
            OutlinedTextField(
                value = bolt11,
                onValueChange = { bolt11 = it },
                label = { Text("BOLT11 Fatura") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (bolt11.isNotBlank()) onPay(bolt11) }) { Text("Öde") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

@Composable
private fun CreateInvoiceDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onCreate: (Long, String) -> Unit,
) {
    var amountSats by remember { mutableLongStateOf(0L) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fatura Oluştur") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = if (amountSats == 0L) "" else amountSats.toString(),
                    onValueChange = { amountSats = it.toLongOrNull() ?: 0L },
                    label = { Text("Miktar (sat)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (amountSats > 0) onCreate(amountSats, description) },
                enabled = !isLoading,
            ) { Text(if (isLoading) "Oluşturuluyor…" else "Oluştur") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

@Composable
private fun PaymentList(payments: List<PaymentUiModel>) {
    if (payments.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Henüz işlem yok.")
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        items(payments) { payment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val prefix = if (payment.type == "sent") "−" else "+"
                Text("$prefix${payment.amountSats} sat")
                Text(payment.status, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
