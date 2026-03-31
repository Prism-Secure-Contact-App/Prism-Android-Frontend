/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.draft

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.room.draft.ComposerDraft

@ContributesBinding(RoomScope::class)
class DefaultComposerDraftService(
    private val volatileComposerDraftStore: VolatileComposerDraftStore,
    private val prismComposerDraftStore: PRISMComposerDraftStore,
) : ComposerDraftService {
    override suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?, isVolatile: Boolean): ComposerDraft? {
        return getStore(isVolatile).loadDraft(roomId, threadRoot)
    }

    override suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?, isVolatile: Boolean) {
        getStore(isVolatile).updateDraft(roomId, threadRoot, draft)
    }

    private fun getStore(isVolatile: Boolean): ComposerDraftStore {
        return if (isVolatile) {
            volatileComposerDraftStore
        } else {
            prismComposerDraftStore
        }
    }
}
