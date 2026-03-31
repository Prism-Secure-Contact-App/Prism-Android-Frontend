/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.test.actions

import io.prism.android.features.poll.api.actions.SendPollResponseAction
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.timeline.Timeline

class FakeSendPollResponseAction : SendPollResponseAction {
    private var executionCount = 0

    fun verifyExecutionCount(count: Int) {
        assert(executionCount == count)
    }

    override suspend fun execute(timeline: Timeline, pollStartId: EventId, answerId: String): Result<Unit> {
        executionCount++
        return Result.success(Unit)
    }
}
