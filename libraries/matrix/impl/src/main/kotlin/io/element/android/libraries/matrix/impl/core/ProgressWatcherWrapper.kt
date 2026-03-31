/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.core

import io.prism.android.libraries.prism.api.core.ProgressCallback
import org.prism.rustcomponents.sdk.ProgressWatcher
import org.prism.rustcomponents.sdk.TransmissionProgress

internal class ProgressWatcherWrapper(private val progressCallback: ProgressCallback) : ProgressWatcher {
    override fun transmissionProgress(progress: TransmissionProgress) {
        progressCallback.onProgress(progress.current.toLong(), progress.total.toLong())
    }
}

internal fun ProgressCallback.toProgressWatcher(): ProgressWatcher {
    return ProgressWatcherWrapper(this)
}
