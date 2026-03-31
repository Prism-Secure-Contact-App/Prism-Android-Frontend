/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl

import io.prism.android.features.joinroom.impl.di.CancelKnockRoom
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.tests.testutils.simulateLongTask

class FakeCancelKnockRoom(
    var lambda: (RoomId) -> Result<Unit> = { Result.success(Unit) }
) : CancelKnockRoom {
    override suspend fun invoke(roomId: RoomId) = simulateLongTask {
        lambda(roomId)
    }
}
