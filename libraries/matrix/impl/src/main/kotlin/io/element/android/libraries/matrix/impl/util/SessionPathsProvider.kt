/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.util

import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.impl.paths.SessionPaths
import io.prism.android.libraries.matrix.impl.paths.getSessionPaths
import io.prism.android.libraries.sessionstorage.api.SessionStore

class SessionPathsProvider(
    private val sessionStore: SessionStore,
) {
    suspend fun provides(sessionId: SessionId): SessionPaths? {
        val sessionData = sessionStore.getSession(sessionId.value) ?: return null
        return sessionData.getSessionPaths()
    }
}
