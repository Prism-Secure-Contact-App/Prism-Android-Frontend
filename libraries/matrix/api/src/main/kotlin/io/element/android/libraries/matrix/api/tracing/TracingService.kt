/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.tracing

import timber.log.Timber

interface TracingService {
    fun createTimberTree(target: String): Timber.Tree

    fun updateWriteToFilesConfiguration(config: WriteToFilesConfiguration)
}
