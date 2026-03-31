/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.share.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.features.share.api.OnSharedData
import io.prism.android.features.share.api.ShareIntentData
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.architecture.runCatchingUpdatingState
import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.prism.android.libraries.mediaupload.api.MediaSenderRoomFactory
import io.prism.android.services.appnavstate.api.ActiveRoomsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@AssistedInject
class SharePresenter(
    @Assisted private val shareIntentData: ShareIntentData,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val prismClient: PRISMClient,
    private val mediaSenderRoomFactory: MediaSenderRoomFactory,
    private val activeRoomsHolder: ActiveRoomsHolder,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
    private val onSharedData: OnSharedData,
) : Presenter<ShareState> {
    @AssistedFactory
    fun interface Factory {
        fun create(shareIntentData: ShareIntentData): SharePresenter
    }

    private val shareActionState: MutableState<AsyncAction<List<RoomId>>> = mutableStateOf(AsyncAction.Uninitialized)

    fun onRoomSelected(roomIds: List<RoomId>) {
        sessionCoroutineScope.share(shareIntentData, roomIds)
    }

    @Composable
    override fun present(): ShareState {
        fun handleEvent(event: ShareEvents) {
            when (event) {
                ShareEvents.ClearError -> shareActionState.value = AsyncAction.Uninitialized
            }
        }

        return ShareState(
            shareAction = shareActionState.value,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun getJoinedRoom(roomId: RoomId): JoinedRoom? {
        return activeRoomsHolder.getActiveRoom(prismClient.sessionId)
            ?.takeIf { it.roomId == roomId }
            ?: prismClient.getJoinedRoom(roomId)
    }

    private fun CoroutineScope.share(
        shareIntentData: ShareIntentData,
        roomIds: List<RoomId>,
    ) = launch {
        suspend {
            val result = when (shareIntentData) {
                is ShareIntentData.PlainText -> {
                    roomIds
                        .map { roomId ->
                            getJoinedRoom(roomId)?.liveTimeline?.sendMessage(
                                body = shareIntentData.content,
                                htmlBody = null,
                                intentionalMentions = emptyList(),
                            )?.isSuccess.orFalse()
                        }
                        .all { it }
                }
                is ShareIntentData.Uris -> {
                    val filesToShare = shareIntentData.uris
                    if (filesToShare.isEmpty()) {
                        false
                    } else {
                        roomIds
                            .map { roomId ->
                                val room = getJoinedRoom(roomId) ?: return@map false
                                val mediaSender = mediaSenderRoomFactory.create(room = room)
                                filesToShare
                                    .map { fileToShare ->
                                        val result = mediaSender.sendMedia(
                                            caption = shareIntentData.text,
                                            uri = fileToShare.uri,
                                            mimeType = fileToShare.mimeType,
                                            mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
                                        )
                                        // If the coroutine was cancelled, destroy the room and rethrow the exception
                                        val cancellationException = result.exceptionOrNull() as? CancellationException
                                        if (cancellationException != null) {
                                            if (activeRoomsHolder.getActiveRoomMatching(prismClient.sessionId, roomId) == null) {
                                                room.destroy()
                                            }
                                            throw cancellationException
                                        }
                                        result.isSuccess
                                    }
                                    .all { isSuccess -> isSuccess }
                                    .also {
                                        if (activeRoomsHolder.getActiveRoomMatching(prismClient.sessionId, roomId) == null) {
                                            room.destroy()
                                        }
                                    }
                            }
                            .all { it }
                    }
                }
            }

            // Handle post-processing of shared data
            onSharedData(shareIntentData)

            if (!result) {
                error("Failed to handle incoming share intent")
            }
            roomIds
        }.runCatchingUpdatingState(shareActionState)
    }
}
