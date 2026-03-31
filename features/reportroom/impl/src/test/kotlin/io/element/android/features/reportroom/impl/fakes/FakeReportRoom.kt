/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.reportroom.impl.fakes

import io.prism.android.features.reportroom.impl.ReportRoom
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.simulateLongTask

class FakeReportRoom(
    var lambda: (RoomId, Boolean, String, Boolean) -> Result<Unit> = { _, _, _, _ -> lambdaError() }
) : ReportRoom {
    override suspend fun invoke(
        roomId: RoomId,
        shouldReport: Boolean,
        reason: String,
        shouldLeave: Boolean
    ): Result<Unit> = simulateLongTask {
        lambda(roomId, shouldReport, reason, shouldLeave)
    }
}
