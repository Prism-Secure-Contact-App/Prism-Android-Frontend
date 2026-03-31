/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.draft

import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.room.draft.ComposerDraft

class FakeComposerDraftService : ComposerDraftService {
    var loadDraftLambda: (RoomId, ThreadId?, Boolean) -> ComposerDraft? = { _, _, _ -> null }
    override suspend fun loadDraft(
        roomId: RoomId,
        threadRoot: ThreadId?,
        isVolatile: Boolean
    ): ComposerDraft? = loadDraftLambda(roomId, threadRoot, isVolatile)

    var saveDraftLambda: (RoomId, ThreadId?, ComposerDraft?, Boolean) -> Unit = { _, _, _, _ -> }
    override suspend fun updateDraft(
        roomId: RoomId,
        threadRoot: ThreadId?,
        draft: ComposerDraft?,
        isVolatile: Boolean
    ) = saveDraftLambda(roomId, threadRoot, draft, isVolatile)
}
