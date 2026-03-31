/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.roomlist

import io.prism.android.libraries.core.data.tryOrNull
import io.prism.android.libraries.prism.impl.util.cancelAndDestroy
import io.prism.android.libraries.prism.impl.util.mxCallbackFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import org.prism.rustcomponents.sdk.Room
import org.prism.rustcomponents.sdk.RoomListDynamicEntriesController
import org.prism.rustcomponents.sdk.RoomListEntriesDynamicFilterKind
import org.prism.rustcomponents.sdk.RoomListEntriesListener
import org.prism.rustcomponents.sdk.RoomListEntriesUpdate
import org.prism.rustcomponents.sdk.RoomListInterface
import org.prism.rustcomponents.sdk.RoomListLoadingState
import org.prism.rustcomponents.sdk.RoomListLoadingStateListener
import org.prism.rustcomponents.sdk.RoomListServiceInterface
import org.prism.rustcomponents.sdk.RoomListServiceState
import org.prism.rustcomponents.sdk.RoomListServiceStateListener
import org.prism.rustcomponents.sdk.RoomListServiceSyncIndicator
import org.prism.rustcomponents.sdk.RoomListServiceSyncIndicatorListener
import timber.log.Timber

private const val SYNC_INDICATOR_DELAY_BEFORE_SHOWING = 1000u
private const val SYNC_INDICATOR_DELAY_BEFORE_HIDING = 0u

fun RoomListInterface.loadingStateFlow(): Flow<RoomListLoadingState> =
    mxCallbackFlow {
        val listener = object : RoomListLoadingStateListener {
            override fun onUpdate(state: RoomListLoadingState) {
                trySendBlocking(state)
            }
        }
        val result = loadingState(listener)
        try {
            send(result.state)
        } catch (exception: Exception) {
            Timber.d(exception, "loadingStateFlow() initialState failed.")
        }
        result.stateStream
    }.catch {
        Timber.d(it, "loadingStateFlow() failed")
    }.buffer(Channel.UNLIMITED)

internal fun RoomListInterface.entriesFlow(
    pageSize: Int,
    initialFilterKind: RoomListEntriesDynamicFilterKind,
    onControllerCreated: (RoomListDynamicEntriesController) -> Unit,
): Flow<List<RoomListEntriesUpdate>> =
    callbackFlow {
        val listener = object : RoomListEntriesListener {
            override fun onUpdate(roomEntriesUpdate: List<RoomListEntriesUpdate>) {
                trySendBlocking(roomEntriesUpdate)
            }
        }
        val result = entriesWithDynamicAdapters(
            pageSize = pageSize.toUInt(),
            listener = listener,
        )
        val controller = result.controller()
        controller.setFilter(initialFilterKind)
        onControllerCreated(controller)
        awaitClose {
            result.entriesStream().cancelAndDestroy()
            controller.destroy()
            result.destroy()
        }
    }.catch {
        Timber.d(it, "entriesFlow() failed")
    }.buffer(Channel.UNLIMITED)

internal fun RoomListServiceInterface.stateFlow(): Flow<RoomListServiceState> =
    mxCallbackFlow {
        val listener = object : RoomListServiceStateListener {
            override fun onUpdate(state: RoomListServiceState) {
                trySendBlocking(state)
            }
        }
        state(listener)
    }.buffer(Channel.UNLIMITED)

internal fun RoomListServiceInterface.syncIndicator(): Flow<RoomListServiceSyncIndicator> =
    mxCallbackFlow {
        val listener = object : RoomListServiceSyncIndicatorListener {
            override fun onUpdate(syncIndicator: RoomListServiceSyncIndicator) {
                trySendBlocking(syncIndicator)
            }
        }
        syncIndicator(
            SYNC_INDICATOR_DELAY_BEFORE_SHOWING,
            SYNC_INDICATOR_DELAY_BEFORE_HIDING,
            listener,
        )
    }.buffer(Channel.UNLIMITED)

internal fun RoomListServiceInterface.roomOrNull(roomId: String): Room? {
    return tryOrNull(
        onException = { Timber.e(it, "Failed finding room with id=$roomId.") }
    ) {
        room(roomId)
    }
}
