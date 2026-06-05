/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformWhile
import org.json.JSONObject
import timber.log.Timber

/**
 * Reusable helper that drives the post-registration bridge wizard.
 *
 * The mautrix bridges (`mautrix-whatsapp`, `mautrix-meta`) expose a chat-bot
 * interface: a user starts a Matrix DM with the bot (e.g. `@whatsappbot:matrix.fathertkt.uk`)
 * and types commands like `login qr`, `login facebook`, etc. The bot replies with
 * QR-code images, status text, or further prompts. PRISM v1.0.0 wraps that flow
 * inside our own onboarding screens so the user never has to know the bot exists.
 *
 * `BridgeBotInteractor` is purely procedural glue:
 *  - [openBotRoom] finds an existing DM with the bot or creates one.
 *  - [sendCommand] posts a plain-text message into that room.
 *  - [observeBotEvents] streams the bot's reply events as they land in the
 *    live timeline, lifted into a small sealed [BotEvent] hierarchy.
 *
 * The presenter is responsible for translating [BotEvent]s into UI state
 * (showing the QR, advancing to the next step on success, etc.).
 */
internal class BridgeBotInteractor(
    private val matrixClient: PRISMClient,
) {
    /**
     * Returns the room id of the DM with [botUserId], creating one if needed.
     */
    suspend fun openBotRoom(botUserId: UserId): Result<RoomId> {
        // Reuse an existing DM when present; mautrix bots are 1:1 so there is at
        // most one such room per user. If the lookup fails for any reason we fall
        // through to creating a fresh DM so the wizard never gets stuck.
        val existing = matrixClient.findDM(botUserId).getOrNull()
        if (existing != null) {
            Timber.d("BridgeBotInteractor: reusing DM ${existing.value} with ${botUserId.value}")
            return Result.success(existing)
        }
        Timber.d("BridgeBotInteractor: creating new DM with ${botUserId.value}")
        return matrixClient.createDM(botUserId)
    }

    /**
     * Sends a plain-text command to the bot. Returns failure if the room cannot
     * be loaded or the SDK rejects the send (e.g. encryption setup not yet done).
     */
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
     * Streams the bot's incoming events from the room's live timeline. Only
     * messages whose sender matches [botUserId] are emitted. The flow
     * completes when [stopWhen] returns true for an emitted event, allowing
     * the caller to fold the success message into a single terminal state.
     */
    fun observeBotEvents(
        roomId: RoomId,
        botUserId: UserId,
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

    /**
     * mautrix bridges (whatsapp, meta, ...) embed a structured login spec inside the message
     * event content under the `fi.mau.bridge.next_step` key. The visible body is just a human
     * readable instruction; the actual code / cookies / data live in this field.
     *
     * Example payloads we care about:
     *   step_id=fi.mau.whatsapp.login.code  type=display_and_wait  display_and_wait.data=N24L-RW1K
     *   step_id=fi.mau.meta.cookies         type=cookies           cookies.url=https://www.instagram.com/...
     *
     * We pull the values into a small typed struct so flows can match on stepId without re-parsing.
     */
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

/** Decoded `fi.mau.bridge.next_step` payload — see [BridgeBotInteractor.parseBridgeNextStep]. */
internal data class BridgeNextStep(
    val stepId: String,
    val type: String,
    /** Convenience: pre-extracted `display_and_wait.data` (e.g. WhatsApp pairing code). */
    val data: String?,
    /** Full structured payload for flows that need richer fields (e.g. meta cookies spec). */
    val raw: JSONObject,
)

/** Strongly-typed lift of the bot's reply events the wizard cares about. */
internal sealed interface BotEvent {
    /** Bot uploaded an image (typically a QR code). */
    data class Image(val source: MediaSource, val caption: String) : BotEvent

    /** Bot sent a text or notice message. */
    data class Text(
        val body: String,
        val raw: EventTimelineItem,
        /** Pre-parsed `fi.mau.bridge.next_step` payload, when present. Null for ordinary chatter. */
        val nextStep: BridgeNextStep? = null,
    ) : BotEvent
}
