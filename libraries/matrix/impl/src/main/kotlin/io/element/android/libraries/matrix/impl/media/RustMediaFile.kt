/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.media

import io.prism.android.libraries.prism.api.media.MediaFile
import org.prism.rustcomponents.sdk.MediaFileHandle

class RustMediaFile(private val inner: MediaFileHandle) : MediaFile {
    override fun path(): String {
        return inner.path()
    }

    override fun persist(path: String): Boolean {
        return inner.persist(path)
    }

    override fun close() {
        inner.close()
    }
}
