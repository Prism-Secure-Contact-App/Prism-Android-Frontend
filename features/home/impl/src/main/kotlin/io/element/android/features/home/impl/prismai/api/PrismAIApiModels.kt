/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrismAIChatRequest(
    val message: String,
    @SerialName("room_id")
    val roomId: String? = null,
)

@Serializable
data class PrismAIChatResponse(
    val success: Boolean,
    @SerialName("user_id")
    val userId: String,
    @SerialName("room_id")
    val roomId: String,
    @SerialName("sent_event_id")
    val sentEventId: String,
    val response: PrismAIMessageEvent? = null,
    val notice: String? = null,
)

@Serializable
data class PrismAIMessageEvent(
    @SerialName("event_id")
    val eventId: String? = null,
    val sender: String? = null,
    val content: PrismAIMessageContent? = null,
    @SerialName("origin_server_ts")
    val originServerTs: Long? = null,
)

@Serializable
data class PrismAIMessageContent(
    @SerialName("msgtype")
    val msgType: String? = null,
    val body: String? = null,
)

@Serializable
data class PrismAIApiKeyResponse(
    @SerialName("api_key")
    val apiKey: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    val created: Boolean = false,
    val deleted: Boolean = false,
)

@Serializable
data class PrismAIRoomsResponse(
    val rooms: List<PrismAIRoomInfo>,
    @SerialName("user_id")
    val userId: String,
)

@Serializable
data class PrismAIRoomInfo(
    @SerialName("room_id")
    val roomId: String,
    val name: String,
    @SerialName("meta_ai_user_id")
    val metaAiUserId: String,
)

@Serializable
data class PrismAIChatHistoryResponse(
    @SerialName("room_id")
    val roomId: String,
    val history: List<PrismAIHistoryEntry>,
)

@Serializable
data class PrismAIHistoryEntry(
    val role: String,
    val content: String,
    val timestamp: Long? = null,
)

@Serializable
data class PrismAILiveMessagesResponse(
    @SerialName("room_id")
    val roomId: String,
    val messages: List<PrismAILiveMessage>,
)

@Serializable
data class PrismAILiveMessage(
    @SerialName("event_id")
    val eventId: String? = null,
    val sender: String,
    val body: String,
    val timestamp: Long? = null,
)
