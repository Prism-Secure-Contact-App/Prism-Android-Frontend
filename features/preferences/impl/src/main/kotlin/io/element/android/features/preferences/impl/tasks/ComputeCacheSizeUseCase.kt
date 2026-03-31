/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.tasks

import android.content.Context
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.androidutils.file.getSizeOfFiles
import io.prism.android.libraries.androidutils.filesize.FileSizeFormatter
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.prism.api.PRISMClient
import kotlinx.coroutines.withContext

interface ComputeCacheSizeUseCase {
    suspend operator fun invoke(): String
}

@ContributesBinding(SessionScope::class)
class DefaultComputeCacheSizeUseCase(
    @ApplicationContext private val context: Context,
    private val prismClient: PRISMClient,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val fileSizeFormatter: FileSizeFormatter,
) : ComputeCacheSizeUseCase {
    override suspend fun invoke(): String = withContext(coroutineDispatchers.io) {
        var cumulativeSize = 0L
        cumulativeSize += prismClient.getCacheSize()
        // - 4096 to not include the size fo the folder
        cumulativeSize += (context.cacheDir.getSizeOfFiles() - 4096).coerceAtLeast(0)
        fileSizeFormatter.format(cumulativeSize)
    }
}
