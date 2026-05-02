/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.media

import coil3.ImageLoader
import io.prism.android.libraries.matrix.api.media.PRISMMediaLoader
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeImageLoaderFactory(
    private val newImageLoaderLambda: () -> ImageLoader = { lambdaError() },
    private val newMatrixImageLoaderLambda: (PRISMMediaLoader) -> ImageLoader = { lambdaError() },
) : ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return newImageLoaderLambda()
    }

    override fun newImageLoader(matrixMediaLoader: PRISMMediaLoader): ImageLoader {
        return newMatrixImageLoaderLambda(matrixMediaLoader)
    }
}
