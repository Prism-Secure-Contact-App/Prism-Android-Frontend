/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.paths

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.di.BaseDirectory
import io.prism.android.libraries.di.CacheDirectory
import java.io.File
import java.util.UUID

@Inject
class SessionPathsFactory(
    @BaseDirectory private val baseDirectory: File,
    @CacheDirectory private val cacheDirectory: File,
) {
    fun create(): SessionPaths {
        val subPath = UUID.randomUUID().toString()
        return SessionPaths(
            fileDirectory = File(baseDirectory, subPath),
            cacheDirectory = File(cacheDirectory, subPath),
        )
    }
}
