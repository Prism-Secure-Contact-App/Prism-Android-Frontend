/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.draft

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.room.draft.ComposerDraft
import timber.log.Timber

/**
 * A draft store that persists drafts in the room state.
 * It can be used to store drafts that should be persisted across app restarts.
 */
@Inject
class PRISMComposerDraftStore(
    private val client: PRISMClient,
) : ComposerDraftStore {
    override suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?): ComposerDraft? {
        return client.getRoom(roomId)?.use { room ->
            room.loadComposerDraft(threadRoot)
                .onFailure {
                    Timber.e(it, "Failed to load composer draft for room $roomId")
                }
                .onSuccess { draft ->
                    room.clearComposerDraft(threadRoot)
                    Timber.d("Loaded composer draft for room $roomId : $draft")
                }
                .getOrNull()
        }
    }

    override suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?) {
        client.getRoom(roomId)?.use { room ->
            val updateDraftResult = if (draft == null) {
                room.clearComposerDraft(threadRoot)
            } else {
                room.saveComposerDraft(draft, threadRoot)
            }
            updateDraftResult
                .onFailure {
                    Timber.e(it, "Failed to update composer draft for room $roomId")
                }
                .onSuccess {
                    Timber.d("Updated composer draft for room $roomId")
                }
        }
    }
}
