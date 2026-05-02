/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.wellknown.test

import io.prism.android.libraries.wellknown.api.ElementWellKnown
import io.prism.android.libraries.wellknown.api.SessionWellknownRetriever
import io.prism.android.libraries.wellknown.api.WellknownRetrieverResult
import io.prism.android.tests.testutils.simulateLongTask

class FakeSessionWellknownRetriever(
    private val getElementWellKnownResult: () -> WellknownRetrieverResult<ElementWellKnown> = { WellknownRetrieverResult.NotFound },
) : SessionWellknownRetriever {
    override suspend fun getElementWellKnown(): WellknownRetrieverResult<ElementWellKnown> = simulateLongTask {
        getElementWellKnownResult()
    }
}
