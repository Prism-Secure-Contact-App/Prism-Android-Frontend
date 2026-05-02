/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.draft

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.ThreadId
import io.prism.android.libraries.matrix.api.room.draft.ComposerDraft

interface ComposerDraftService {
    suspend fun loadDraft(roomId: RoomId, threadRoot: ThreadId?, isVolatile: Boolean): ComposerDraft?
    suspend fun updateDraft(roomId: RoomId, threadRoot: ThreadId?, draft: ComposerDraft?, isVolatile: Boolean)
}
