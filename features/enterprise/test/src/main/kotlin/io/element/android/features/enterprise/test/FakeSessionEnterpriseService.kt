/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.enterprise.test

import io.prism.android.features.enterprise.api.SessionEnterpriseService
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.simulateLongTask

class FakeSessionEnterpriseService(
    private val isPRISMCallAvailableResult: () -> Boolean = { lambdaError() },
) : SessionEnterpriseService {
    override suspend fun init() {
    }

    override suspend fun isPRISMCallAvailable(): Boolean = simulateLongTask {
        isPRISMCallAvailableResult()
    }
}
