/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.bridge

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PRISM Bridge Status Manager
 *
 * WhatsApp ve Instagram köprülerinin bağlantı durumunu takip eder.
 * Mautrix köprüleri Matrix bot mesajları üzerinden durum eventleri gönderir;
 * bu sınıf o event'leri dinleyerek UI'ın reaktif biçimde güncellemesini sağlar.
 *
 * Kullanım:
 *   - [onStatusReceived] çağrısı: Matrix bridge bot'undan gelen ham durum string'ini işler.
 *   - [bridgeStatuses] Flow'u: Compose/ViewModel katmanında collect edilir.
 */
class BridgeStatusManager {

    private val _bridgeStatuses = MutableStateFlow<Map<String, BridgeStatus>>(emptyMap())
    val bridgeStatuses: StateFlow<Map<String, BridgeStatus>> = _bridgeStatuses.asStateFlow()

    enum class BridgeStatus {
        /** Köprü bağlı, mesajlar akıyor. */
        CONNECTED,
        /** Köprü bağlanmaya çalışıyor. */
        CONNECTING,
        /** Bağlantı koptu. */
        DISCONNECTED,
        /** WhatsApp/Instagram oturumu sona erdi, yeniden giriş gerekiyor. */
        AUTH_REQUIRED,
    }

    /**
     * Mautrix bridge bot'undan gelen durum mesajını işler.
     *
     * @param platform "whatsapp" veya "instagram"
     * @param rawStatus Köprüden gelen ham durum string'i
     *   ("connected", "connecting", "disconnected", "auth_required", vb.)
     */
    fun onStatusReceived(platform: String, rawStatus: String) {
        val status = when (rawStatus.lowercase()) {
            "connected", "logged_in" -> BridgeStatus.CONNECTED
            "connecting" -> BridgeStatus.CONNECTING
            "auth_required", "logged_out", "not_logged_in" -> BridgeStatus.AUTH_REQUIRED
            else -> BridgeStatus.DISCONNECTED
        }
        _bridgeStatuses.value = _bridgeStatuses.value + (platform to status)
    }

    fun getStatus(platform: String): BridgeStatus {
        return _bridgeStatuses.value[platform] ?: BridgeStatus.DISCONNECTED
    }

    fun isAnyBridgeConnected(): Boolean {
        return _bridgeStatuses.value.values.any { it == BridgeStatus.CONNECTED }
    }
}
