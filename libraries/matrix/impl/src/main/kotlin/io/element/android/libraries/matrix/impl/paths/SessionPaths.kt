/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.paths

import io.prism.android.libraries.sessionstorage.api.SessionData
import java.io.File

data class SessionPaths(
    val fileDirectory: File,
    val cacheDirectory: File,
) {
    fun deleteRecursively() {
        fileDirectory.deleteRecursively()
        cacheDirectory.deleteRecursively()
    }
}

internal fun SessionData.getSessionPaths(): SessionPaths {
    return SessionPaths(
        fileDirectory = File(sessionPath),
        cacheDirectory = File(cachePath),
    )
}
