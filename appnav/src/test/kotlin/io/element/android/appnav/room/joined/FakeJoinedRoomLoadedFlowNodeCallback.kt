/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.room.joined

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.permalink.PermalinkData
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeJoinedRoomLoadedFlowNodeCallback : JoinedRoomLoadedFlowNode.Callback {
    override fun navigateToRoom(roomId: RoomId, serverNames: List<String>) = lambdaError()
    override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) = lambdaError()
    override fun navigateToGlobalNotificationSettings() = lambdaError()
}
