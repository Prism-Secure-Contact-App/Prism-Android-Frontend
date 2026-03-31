/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl.fake

import io.prism.android.features.invite.impl.AcceptInvite
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.simulateLongTask

class FakeAcceptInvite(
    private val lambda: (RoomId) -> Result<RoomId> = { lambdaError() },
) : AcceptInvite {
    override suspend fun invoke(roomId: RoomId) = simulateLongTask {
        lambda(roomId)
    }
}
