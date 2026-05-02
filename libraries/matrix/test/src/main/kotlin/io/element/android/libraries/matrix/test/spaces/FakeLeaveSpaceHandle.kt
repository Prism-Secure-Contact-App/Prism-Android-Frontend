/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.test.spaces

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.spaces.LeaveSpaceHandle
import io.prism.android.libraries.matrix.api.spaces.LeaveSpaceRoom
import io.prism.android.libraries.matrix.test.A_SPACE_ID
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.simulateLongTask

class FakeLeaveSpaceHandle(
    override val id: RoomId = A_SPACE_ID,
    private val roomsResult: () -> Result<List<LeaveSpaceRoom>> = { lambdaError() },
    private val leaveResult: (List<RoomId>) -> Result<Unit> = { lambdaError() },
    private val closeResult: () -> Unit = { lambdaError() },
) : LeaveSpaceHandle {
    override suspend fun rooms(): Result<List<LeaveSpaceRoom>> = simulateLongTask {
        roomsResult()
    }

    override suspend fun leave(roomIds: List<RoomId>): Result<Unit> = simulateLongTask {
        leaveResult(roomIds)
    }

    override fun close() {
        return closeResult()
    }
}
