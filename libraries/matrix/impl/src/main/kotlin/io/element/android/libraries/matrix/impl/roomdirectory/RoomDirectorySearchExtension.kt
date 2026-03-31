/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.roomdirectory

import io.prism.android.libraries.prism.impl.util.cancelAndDestroy
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import org.prism.rustcomponents.sdk.RoomDirectorySearch
import org.prism.rustcomponents.sdk.RoomDirectorySearchEntriesListener
import org.prism.rustcomponents.sdk.RoomDirectorySearchEntryUpdate
import timber.log.Timber

internal fun RoomDirectorySearch.resultsFlow(): Flow<List<RoomDirectorySearchEntryUpdate>> =
    callbackFlow {
        val listener = object : RoomDirectorySearchEntriesListener {
            override fun onUpdate(roomEntriesUpdate: List<RoomDirectorySearchEntryUpdate>) {
                trySendBlocking(roomEntriesUpdate)
            }
        }
        val result = results(listener)
        awaitClose {
            result.cancelAndDestroy()
        }
    }.catch {
        Timber.d(it, "timelineDiffFlow() failed")
    }.buffer(Channel.UNLIMITED)
