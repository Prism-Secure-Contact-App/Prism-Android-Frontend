/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl

import io.prism.android.features.joinroom.impl.di.KnockRoom
import io.prism.android.libraries.matrix.api.core.RoomIdOrAlias
import io.prism.android.tests.testutils.simulateLongTask

class FakeKnockRoom(
    var lambda: (RoomIdOrAlias, String, List<String>) -> Result<Unit> = { _, _, _ -> Result.success(Unit) }
) : KnockRoom {
    override suspend fun invoke(roomIdOrAlias: RoomIdOrAlias, message: String, serverNames: List<String>): Result<Unit> = simulateLongTask {
        lambda(roomIdOrAlias, message, serverNames)
    }
}
