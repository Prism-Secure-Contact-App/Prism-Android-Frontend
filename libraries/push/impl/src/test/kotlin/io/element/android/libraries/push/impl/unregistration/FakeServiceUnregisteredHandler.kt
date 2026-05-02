/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.push.impl.unregistration

import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeServiceUnregisteredHandler(
    private val handleResult: (UserId) -> Unit = { lambdaError() },
) : ServiceUnregisteredHandler {
    override suspend fun handle(userId: UserId) {
        handleResult(userId)
    }
}
