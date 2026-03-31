/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import kotlinx.coroutines.withContext
import timber.log.Timber

interface MarkAsFullyRead {
    suspend operator fun invoke(roomId: RoomId, eventId: EventId): Result<Unit>
}

@ContributesBinding(SessionScope::class)
class DefaultMarkAsFullyRead(
    private val prismClient: PRISMClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : MarkAsFullyRead {
    override suspend fun invoke(roomId: RoomId, eventId: EventId): Result<Unit> = withContext(coroutineDispatchers.io) {
        prismClient.markRoomAsFullyRead(roomId, eventId).onFailure {
            Timber.e(it, "Failed to mark room $roomId as fully read for event $eventId")
        }
    }
}
