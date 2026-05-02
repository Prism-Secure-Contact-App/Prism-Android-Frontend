/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.media

import coil3.ImageLoader
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.SessionId

interface ImageLoaderHolder {
    fun get(): ImageLoader
    fun get(client: PRISMClient): ImageLoader
    fun remove(sessionId: SessionId)
}
