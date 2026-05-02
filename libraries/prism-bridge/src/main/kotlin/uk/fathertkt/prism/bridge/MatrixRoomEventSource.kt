/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.bridge

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Matrix SDK üzerinden mesajları çeken [RoomEventSource] implementasyonu.
 *
 * Bir room ID verildiğinde o room'un canlı timeline'ını (liveTimeline) dinler;
 * yalnızca metin mesajlarını ([MessageContent]) alır, [BridgedMessage]'a map eder
 * ve bir Flow olarak yayar.
 *
 * Platform etiketi ("whatsapp" / "instagram") bu sınıfta belirlenmez;
 * [UnifiedTimelineManager] hangi room ID'nin hangi platforma ait olduğunu bildiğinden
 * birleştirme sırasında etiketi kendisi atar.
 *
 * @param matrixClient Oturumu açık olan Matrix istemcisi. DI ile inject edilir.
 */
class MatrixRoomEventSource(
    private val matrixClient: PRISMClient,
) : RoomEventSource {

    /**
     * Verilen [roomId] için mesajların canlı akışını döner.
     *
     * - Room bulunamazsa (bridge henüz katılmamış / ID yanlış) boş liste yayar.
     * - Yalnızca metin içerikli event'ler ([MessageContent]) dahil edilir;
     *   sistem mesajları, tepkiler, silinmiş mesajlar filtrelenir.
     */
    override fun getMessages(roomId: String): Flow<List<BridgedMessage>> = flow {
        val room = matrixClient.getJoinedRoom(RoomId(roomId))
        if (room == null) {
            emit(emptyList())
            return@flow
        }
        emitAll(
            room.liveTimeline.timelineItems.map { items ->
                items.mapNotNull { item -> item.toBridgedMessage(roomId) }
            }
        )
    }
}

// ─── Mapping helper ──────────────────────────────────────────────────────────

/**
 * [PRISMTimelineItem]'ı [BridgedMessage]'a dönüştürür.
 * Event değilse veya mesaj içeriği yoksa null döner (filtre olarak kullanılır).
 */
private fun PRISMTimelineItem.toBridgedMessage(roomId: String): BridgedMessage? {
    if (this !is PRISMTimelineItem.Event) return null
    val messageContent = event.content as? MessageContent ?: return null
    val eventId = event.eventId?.value ?: return null

    return BridgedMessage(
        eventId = eventId,
        senderId = event.sender.value,
        timestamp = event.timestamp,
        content = messageContent.body,
        platform = "",   // UnifiedTimelineManager tarafından set edilir
        roomId = roomId,
    )
}
