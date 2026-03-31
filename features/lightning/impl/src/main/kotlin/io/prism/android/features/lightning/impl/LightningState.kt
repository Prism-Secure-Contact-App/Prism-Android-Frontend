/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class LightningState(
    val isConnected: Boolean = false,
    /** Kullanılabilir bakiye (satoshi) */
    val balanceSats: Long = 0L,
    val payments: ImmutableList<PaymentUiModel> = persistentListOf(),
    /** Oluşturulmuş BOLT11 fatura (kullanıcı paylaşmak için) */
    val generatedInvoice: String? = null,
    val isCreatingInvoice: Boolean = false,
    val error: String? = null,
    val eventSink: (LightningEvent) -> Unit,
)

data class PaymentUiModel(
    val amountSats: Long,
    val type: String,       // "sent" | "received"
    val status: String,     // "success" | "pending" | "failed"
    val timestampMs: Long,
)
