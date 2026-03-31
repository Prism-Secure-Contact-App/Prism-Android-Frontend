/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.architecture.Presenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import uk.fathertkt.prism.lightning.LightningWalletModule

@Inject
class LightningPresenter(
    private val wallet: LightningWalletModule,
) : Presenter<LightningState> {

    @Composable
    override fun present(): LightningState {
        val coroutineScope = rememberCoroutineScope()

        val isConnected by wallet.isConnected.collectAsState()
        val balanceSats by wallet.balanceSats.collectAsState()
        val sdkPayments by wallet.payments.collectAsState()

        var generatedInvoice by remember { mutableStateOf<String?>(null) }
        var isCreatingInvoice by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }

        val payments = remember(sdkPayments) {
            sdkPayments.map { p ->
                PaymentUiModel(
                    amountSats = p.amountMsat / 1000,
                    type = p.paymentType.name.lowercase(),
                    status = p.status.name.lowercase(),
                    timestampMs = p.paymentTime * 1000,
                )
            }.toImmutableList()
        }

        fun handleEvent(event: LightningEvent) {
            when (event) {
                is LightningEvent.PayInvoice -> {
                    coroutineScope.launch {
                        val success = wallet.payInvoice(event.bolt11)
                        if (!success) error = "Ödeme başarısız. Faturayı kontrol edin."
                    }
                }
                is LightningEvent.CreateInvoice -> {
                    coroutineScope.launch {
                        isCreatingInvoice = true
                        val invoice = wallet.createInvoice(event.amountSats, event.description)
                        generatedInvoice = invoice?.bolt11
                        isCreatingInvoice = false
                        if (invoice == null) error = "Fatura oluşturulamadı."
                    }
                }
                LightningEvent.DismissInvoice -> {
                    generatedInvoice = null
                }
                LightningEvent.Refresh -> {
                    wallet.refreshBalance()
                }
                LightningEvent.DismissError -> {
                    error = null
                }
            }
        }

        return LightningState(
            isConnected = isConnected,
            balanceSats = balanceSats,
            payments = payments,
            generatedInvoice = generatedInvoice,
            isCreatingInvoice = isCreatingInvoice,
            error = error,
            eventSink = ::handleEvent,
        )
    }
}
