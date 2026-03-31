/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.bridge

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Tek bir kişiyle farklı platformlarda (WhatsApp, Instagram) süregelen
 * sohbetleri zaman damgasına göre sıralı tek bir akış halinde sunar.
 *
 * Matrix SDK bağımlılığını bu modülden dışarıda tutmak için mesaj verisi
 * [RoomEventSource] arayüzü üzerinden sağlanır. Gerçek implementasyon
 * `features/` katmanında Matrix SDK'yı kullanarak yazılır.
 */
class UnifiedTimelineManager(
    private val eventSource: RoomEventSource,
) {

    private val contactMappings = mutableMapOf<String, ContactMapping>()

    /**
     * Bir kişinin farklı platformlardaki room ID'lerini birbirine bağlar.
     *
     * @param name Kişinin görünen adı (anahtar olarak kullanılır)
     * @param waRoomId  WhatsApp bridge room ID'si (yoksa null)
     * @param igRoomId  Instagram bridge room ID'si (yoksa null)
     */
    fun mapContact(name: String, waRoomId: String? = null, igRoomId: String? = null) {
        contactMappings[name] = ContactMapping(name, waRoomId, igRoomId)
    }

    fun removeContact(name: String) {
        contactMappings.remove(name)
    }

    /**
     * Verilen kişinin tüm platformlardaki mesajlarını zaman damgasına göre
     * ters sıralanmış tek bir [Flow] olarak döner.
     */
    fun getUnifiedTimeline(contactName: String): Flow<List<BridgedMessage>> {
        val mapping = contactMappings[contactName] ?: return flowOf(emptyList())

        val waFlow: Flow<List<BridgedMessage>> = mapping.whatsappRoomId
            ?.let { roomId ->
                eventSource.getMessages(roomId).map { msgs ->
                    msgs.map { it.copy(platform = "whatsapp") }
                }
            }
            ?: flowOf(emptyList())

        val igFlow: Flow<List<BridgedMessage>> = mapping.instagramRoomId
            ?.let { roomId ->
                eventSource.getMessages(roomId).map { msgs ->
                    msgs.map { it.copy(platform = "instagram") }
                }
            }
            ?: flowOf(emptyList())

        return combine(waFlow, igFlow) { wa, ig ->
            (wa + ig).sortedByDescending { it.timestamp }
        }
    }

    fun getMappedContacts(): List<ContactMapping> = contactMappings.values.toList()
}

// ─── Veri modelleri ──────────────────────────────────────────────────────────

data class ContactMapping(
    val displayName: String,
    val whatsappRoomId: String?,
    val instagramRoomId: String?,
)

data class BridgedMessage(
    val eventId: String,
    val senderId: String,
    val timestamp: Long,
    val content: String,
    /** "whatsapp" veya "instagram" */
    val platform: String,
    val roomId: String,
)

/**
 * Matrix room'larından mesaj akışı sağlayan arayüz.
 * Gerçek implementasyon Matrix Rust SDK kullanarak `:features` katmanında yapılır.
 */
interface RoomEventSource {
    fun getMessages(roomId: String): Flow<List<BridgedMessage>>
}
