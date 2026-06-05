/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.features.home.impl.prismai.data.PrismAIRepository
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.spaces.SpaceService
import io.prism.android.libraries.preferences.api.store.SessionPreferencesStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@Inject
class PrismAISpacePresenter(
    private val spaceService: SpaceService,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val matrixClient: PRISMClient,
    private val prismAIRepository: PrismAIRepository,
) : Presenter<PrismAISpaceState> {

    @Composable
    override fun present(): PrismAISpaceState {
        val coroutineScope = rememberCoroutineScope()
        var rooms by remember { mutableStateOf<List<io.prism.android.libraries.matrix.api.spaces.SpaceRoom>>(emptyList()) }
        var apiKey by remember { mutableStateOf<String?>(null) }
        var showDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var spaceName by remember { mutableStateOf("PrismAI") }
        var spaceIdValue by remember { mutableStateOf<String?>(null) }
        var chatMessages by remember { mutableStateOf<List<PrismAIChatMessage>>(emptyList()) }
        var chatInput by remember { mutableStateOf("") }
        var isSending by remember { mutableStateOf(false) }
        var chatError by remember { mutableStateOf<String?>(null) }
        var selectedRoomId by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                val id = sessionPreferencesStore.getPrismAISpaceId().first()
                if (id.isNullOrBlank()) {
                    errorMessage = "PrismAI space not found. Connect WhatsApp and send a message to Meta AI first."
                    return@LaunchedEffect
                }
                spaceIdValue = id
                val roomId = RoomId(id)
                val spaceRoom = spaceService.getSpaceRoom(roomId)
                spaceName = spaceRoom?.displayName ?: "PrismAI"

                val spaceRoomList = spaceService.spaceRoomList(roomId)
                spaceRoomList.spaceRoomsFlow.collect { spaceRooms ->
                    rooms = spaceRooms
                    if (selectedRoomId == null && spaceRooms.isNotEmpty()) {
                        selectedRoomId = spaceRooms.firstOrNull()?.roomId?.value
                    }
                }
            } catch (t: Throwable) {
                Timber.e(t, "PrismAISpacePresenter: failed to load space")
                errorMessage = t.message ?: "Failed to load PrismAI space"
            }
        }

        LaunchedEffect(selectedRoomId) {
            selectedRoomId?.let { roomId ->
                try {
                    val result = prismAIRepository.getLiveChatHistory(roomId, limit = 50)
                    result.getOrNull()?.let { response ->
                        chatMessages = response.messages.map { msg ->
                            PrismAIChatMessage(
                                id = msg.eventId ?: UUID.randomUUID().toString(),
                                role = if (msg.sender == matrixClient.userProfile.value.userId.value) "user" else "assistant",
                                content = msg.body,
                                timestamp = msg.timestamp,
                            )
                        }
                    }
                } catch (t: Throwable) {
                    Timber.w(t, "PrismAISpacePresenter: failed to load chat history")
                }
            }
        }

        fun generateKey(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return "prism_sk_" + (1..32).map { chars.random() }.joinToString("")
        }

        fun handleEvent(event: PrismAISpaceEvents) {
            when (event) {
                is PrismAISpaceEvents.GenerateApiKey -> {
                    apiKey = generateKey()
                    showDialog = true
                }
                is PrismAISpaceEvents.CopyApiKey -> {
                    // Clipboard copy handled in View layer
                }
                is PrismAISpaceEvents.DismissKeyDialog -> {
                    showDialog = false
                }
                is PrismAISpaceEvents.RoomClick -> {
                    // Navigate to room handled by parent callback
                }
                is PrismAISpaceEvents.SendChatMessage -> {
                    val roomId = selectedRoomId ?: return
                    val messageText = event.message.trim()
                    if (messageText.isEmpty() || isSending) return

                    val userMessage = PrismAIChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "user",
                        content = messageText,
                    )
                    chatMessages = chatMessages + userMessage
                    chatInput = ""
                    isSending = true
                    chatError = null

                    coroutineScope.launch {
                        try {
                            val result = prismAIRepository.sendChatMessage(messageText, roomId)
                            result.fold(
                                onSuccess = { response ->
                                    response.response?.let { resp ->
                                        val assistantMessage = PrismAIChatMessage(
                                            id = resp.eventId ?: UUID.randomUUID().toString(),
                                            role = "assistant",
                                            content = resp.content?.body ?: "(no response)",
                                            timestamp = resp.originServerTs,
                                        )
                                        chatMessages = chatMessages + assistantMessage
                                    } ?: run {
                                        chatError = response.notice ?: "No response from Meta AI"
                                    }
                                },
                                onFailure = { t ->
                                    chatError = t.message ?: "Failed to send message"
                                    Timber.e(t, "PrismAISpacePresenter: chat send failed")
                                }
                            )
                        } finally {
                            isSending = false
                        }
                    }
                }
                is PrismAISpaceEvents.UpdateChatInput -> {
                    chatInput = event.input
                }
                is PrismAISpaceEvents.SelectRoomForChat -> {
                    selectedRoomId = event.roomId
                    chatMessages = emptyList()
                }
                is PrismAISpaceEvents.LoadChatHistory -> {
                    // Handled by LaunchedEffect(selectedRoomId)
                }
            }
        }

        return if (errorMessage != null) {
            PrismAISpaceState.Error(errorMessage!!)
        } else if (spaceIdValue == null) {
            PrismAISpaceState.Loading
        } else {
            PrismAISpaceState.Loaded(
                spaceName = spaceName,
                rooms = rooms,
                apiKey = apiKey,
                showGenerateKeyDialog = showDialog,
                chatMessages = chatMessages,
                chatInput = chatInput,
                isSending = isSending,
                chatError = chatError,
                selectedRoomId = selectedRoomId,
                eventSink = ::handleEvent,
            )
        }
    }
}
