/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.preferences.impl.bridgesettings

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.media.MediaSource
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.ImageMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.MessageContent
import io.prism.android.libraries.matrix.api.timeline.item.event.NoticeMessageType
import io.prism.android.libraries.matrix.api.timeline.item.event.TextMessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformWhile
import org.json.JSONObject
import timber.log.Timber

internal class BridgeBotInteractor(
    private val matrixClient: PRISMClient,
) {
    suspend fun openBotRoom(botUserId: UserId): Result<RoomId> {
        val existing = matrixClient.findDM(botUserId).getOrNull()
        if (existing != null) {
            Timber.d("BridgeBotInteractor: reusing DM ${existing.value} with ${botUserId.value}")
            return Result.success(existing)
        }
        Timber.d("BridgeBotInteractor: creating new DM with ${botUserId.value}")
        return matrixClient.createDM(botUserId)
    }

    suspend fun sendCommand(roomId: RoomId, command: String): Result<Unit> {
        var room = matrixClient.getJoinedRoom(roomId)
        var attempts = 0
        while (room == null && attempts < 50) {
            kotlinx.coroutines.delay(100)
            room = matrixClient.getJoinedRoom(roomId)
            attempts++
        }
        if (room == null) {
            return Result.failure(IllegalStateException("Bot room $roomId is not joined yet after 5s."))
        }
        return room.liveTimeline.sendMessage(
            body = command,
            htmlBody = null,
            intentionalMentions = emptyList(),
        ).onSuccess { Timber.d("BridgeBotInteractor: sent '$command' to ${roomId.value}") }
    }

    /**
     * Streams bot events. If [skipExisting] is true, the current timeline is scanned once
     * to pre-populate the seen-event set so that ONLY events arriving after this call
     * are emitted. This prevents stale success/pairing-code messages from terminating
     * a fresh observation early.
     */
    fun observeBotEvents(
        roomId: RoomId,
        botUserId: UserId,
        skipExisting: Boolean = false,
        stopWhen: (BotEvent) -> Boolean = { false },
    ): Flow<BotEvent> = flow {
        var room = matrixClient.getJoinedRoom(roomId)
        var attempts = 0
        while (room == null && attempts < 50) {
            kotlinx.coroutines.delay(100)
            room = matrixClient.getJoinedRoom(roomId)
            attempts++
        }
        if (room == null) error("Cannot observe bot events: room $roomId is not joined.")
        val seenEventIds = HashSet<String>()
        if (skipExisting) {
            val currentItems = room.liveTimeline.timelineItems.first()
            currentItems.toBotEvents(botUserId, seenEventIds)
            Timber.d("BridgeBotInteractor: pre-seeded %d existing events", seenEventIds.size)
        }
        room.liveTimeline.timelineItems
            .map { items -> items.toBotEvents(botUserId, seenEventIds) }
            .transformWhile { events ->
                for (e in events) {
                    emit(e)
                    if (stopWhen(e)) return@transformWhile false
                }
                true
            }
            .collect { /* nothing — emission already happened in transformWhile */ }
    }

    private fun List<PRISMTimelineItem>.toBotEvents(botUserId: UserId, seen: HashSet<String>): List<BotEvent> {
        val out = ArrayList<BotEvent>()
        for (item in this) {
            val event = (item as? PRISMTimelineItem.Event)?.event ?: continue
            if (event.sender != botUserId) continue
            val eventKey = event.eventId?.value ?: continue
            if (!seen.add(eventKey)) continue
            val content = event.content as? MessageContent ?: continue
            when (val type = content.type) {
                is ImageMessageType -> out += BotEvent.Image(
                    source = type.source,
                    caption = type.caption ?: content.body,
                )
                is TextMessageType,
                is NoticeMessageType -> {
                    val originalJson = event.timelineItemDebugInfoProvider().originalJson
                    val nextStep = parseBridgeNextStep(originalJson)
                    Timber.d(
                        "BridgeBot bot=%s body=%s originalJsonLen=%d hasNextStep=%s stepId=%s data=%s",
                        botUserId.value,
                        content.body.take(120),
                        originalJson?.length ?: -1,
                        nextStep != null,
                        nextStep?.stepId,
                        nextStep?.data,
                    )
                    out += BotEvent.Text(
                        body = content.body,
                        raw = event,
                        nextStep = nextStep,
                    )
                }
                else -> Unit
            }
        }
        return out
    }

    private fun parseBridgeNextStep(originalJson: String?): BridgeNextStep? {
        if (originalJson.isNullOrBlank()) return null
        return try {
            val root = JSONObject(originalJson)
            val content = root.optJSONObject("content") ?: root
            val ns = content.optJSONObject("fi.mau.bridge.next_step") ?: return null
            val stepId = ns.optString("step_id")
            val type = ns.optString("type")
            val data = when (type) {
                "display_and_wait" -> {
                    ns.optJSONObject("display_and_wait")?.optString("data")
                        ?: ns.optString("data").takeIf { it.isNotBlank() }
                        ?: ns.optJSONObject("data")?.optString("data")
                }
                else -> null
            }
            BridgeNextStep(stepId = stepId, type = type, data = data, raw = ns)
        } catch (t: Throwable) {
            Timber.w(t, "BridgeBotInteractor: failed to parse fi.mau.bridge.next_step")
            null
        }
    }
}

internal data class BridgeNextStep(
    val stepId: String,
    val type: String,
    val data: String?,
    val raw: JSONObject,
)

internal sealed interface BotEvent {
    data class Image(val source: MediaSource, val caption: String) : BotEvent
    data class Text(
        val body: String,
        val raw: EventTimelineItem,
        val nextStep: BridgeNextStep? = null,
    ) : BotEvent
}
