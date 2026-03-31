/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.lightning

import breez_sdk.BlockingBreezServices
import breez_sdk.BreezEvent
import breez_sdk.Config
import breez_sdk.EnvironmentType
import breez_sdk.EventListener
import breez_sdk.GreenlightNodeConfig
import breez_sdk.LnInvoice
import breez_sdk.Network
import breez_sdk.NodeConfig
import breez_sdk.Payment
import breez_sdk.PaymentTypeFilter
import breez_sdk.connect
import breez_sdk.defaultConfig
import breez_sdk.mnemonicToSeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * PRISM Lightning Wallet Module
 *
 * Breez SDK üzerinden Bitcoin Lightning Network ödemelerini yönetir.
 * Breez SDK, arka planda yönetilen bir Greenlight (CLN) node'u çalıştırır;
 * kullanıcı cüzdan seed'ini yönetmek zorunda kalmaz.
 *
 * Kurulum:
 *   1. https://breez.technology adresinden API key alın.
 *   2. Kullanıcıya ait 12/24 kelimelik BIP-39 mnemonic üretin (ilk çalıştırmada).
 *   3. Mnemonic'i cihazda şifreli depolamada (ör. EncryptedSharedPreferences) saklayın.
 *   4. [connect] çağrısıyla SDK'yı başlatın.
 *
 * @param apiKey   Breez dashboard'dan alınan API anahtarı
 * @param mnemonic BIP-39 mnemonic (12 veya 24 kelime)
 */
class LightningWalletModule(
    private val apiKey: String,
    private val mnemonic: String,
) : EventListener {

    private var sdk: BlockingBreezServices? = null

    private val _balanceSats = MutableStateFlow(0L)
    /** Kullanılabilir kanal bakiyesi (satoshi). UI bu Flow'u collect eder. */
    val balanceSats: StateFlow<Long> = _balanceSats.asStateFlow()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    /** Tamamlanan ödeme geçmişi. */
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // ─── Bağlantı ────────────────────────────────────────────────────────────

    /**
     * Breez SDK node'unu başlatır. Uygulama açılışında bir kez çağrılmalıdır.
     * İlk çalıştırmada Greenlight node oluşturulur (~5-10 sn sürebilir).
     */
    fun connect() {
        try {
            val seed = mnemonicToSeed(mnemonic)
            val config: Config = defaultConfig(
                envType = EnvironmentType.PRODUCTION,
                apiKey = apiKey,
                nodeConfig = NodeConfig.Greenlight(
                    config = GreenlightNodeConfig(partnerCredentials = null, inviteCode = null)
                )
            )
            sdk = connect(config, seed, this)
            _isConnected.value = true
            refreshBalance()
        } catch (e: Exception) {
            Timber.e(e, "Lightning SDK bağlantısı kurulamadı")
            _isConnected.value = false
        }
    }

    fun disconnect() {
        sdk?.disconnect()
        sdk = null
        _isConnected.value = false
    }

    // ─── Bakiye ──────────────────────────────────────────────────────────────

    fun refreshBalance() {
        val info = sdk?.nodeInfo() ?: return
        _balanceSats.value = info.channelsBalanceMsat / 1000
    }

    // ─── Ödeme alma ──────────────────────────────────────────────────────────

    /**
     * Gelen ödeme için BOLT11 fatura oluşturur.
     *
     * @param amountSats Alınacak miktar (satoshi)
     * @param description Fatura açıklaması
     * @return BOLT11 fatura string'i veya SDK bağlı değilse null
     */
    fun createInvoice(amountSats: Long, description: String): LnInvoice? {
        return sdk?.receivePayment(amountSats * 1000L, description)?.lnInvoice
    }

    // ─── Ödeme gönderme ──────────────────────────────────────────────────────

    /**
     * Verilen BOLT11 faturasını öder.
     *
     * @param bolt11 Ödeme talebi string'i
     * @return İşlem başarılıysa true
     */
    fun payInvoice(bolt11: String): Boolean {
        return try {
            sdk?.sendPayment(bolt11, null)
            refreshBalance()
            true
        } catch (e: Exception) {
            Timber.e(e, "Lightning ödeme gönderilemedi")
            false
        }
    }

    // ─── Geçmiş ──────────────────────────────────────────────────────────────

    /**
     * Tüm ödeme geçmişini döner (gönderilen + alınan).
     */
    fun fetchPayments(): List<Payment> {
        return sdk?.listPayments(
            filter = PaymentTypeFilter.ALL,
            fromTimestamp = null,
            toTimestamp = null
        ) ?: emptyList()
    }

    // ─── Event listener ──────────────────────────────────────────────────────

    override fun onEvent(e: BreezEvent) {
        when (e) {
            is BreezEvent.InvoicePaid -> {
                refreshBalance()
                _payments.value = fetchPayments()
            }
            is BreezEvent.Synced -> {
                refreshBalance()
                _payments.value = fetchPayments()
            }
            else -> Unit
        }
    }
}
