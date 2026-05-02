/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl

import io.prism.android.features.joinroom.impl.di.ForgetRoom
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.tests.testutils.simulateLongTask

class FakeForgetRoom(
    var lambda: (RoomId) -> Result<Unit> = { Result.success(Unit) }
) : ForgetRoom {
    override suspend fun invoke(roomId: RoomId) = simulateLongTask {
        lambda(roomId)
    }
}
