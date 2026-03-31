/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

sealed interface LightningEvent {
    /** Verilen BOLT11 faturasını öde. */
    data class PayInvoice(val bolt11: String) : LightningEvent

    /** Belirtilen miktar için fatura oluştur. */
    data class CreateInvoice(val amountSats: Long, val description: String) : LightningEvent

    /** Oluşturulan faturayı kapat / temizle. */
    data object DismissInvoice : LightningEvent

    /** Bakiyeyi ve ödeme geçmişini yenile. */
    data object Refresh : LightningEvent

    /** Hata mesajını kapat. */
    data object DismissError : LightningEvent
}
