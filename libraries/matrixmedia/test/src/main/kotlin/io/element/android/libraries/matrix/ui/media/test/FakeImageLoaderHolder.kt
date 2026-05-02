/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.media.test

import coil3.ImageLoader
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.ui.media.ImageLoaderHolder

class FakeImageLoaderHolder(
    val fakeImageLoader: ImageLoader = FakeImageLoader(),
) : ImageLoaderHolder {
    override fun get(): ImageLoader {
        return fakeImageLoader
    }

    override fun get(client: PRISMClient): ImageLoader {
        return fakeImageLoader
    }

    override fun remove(sessionId: SessionId) {
        // No-op
    }
}
