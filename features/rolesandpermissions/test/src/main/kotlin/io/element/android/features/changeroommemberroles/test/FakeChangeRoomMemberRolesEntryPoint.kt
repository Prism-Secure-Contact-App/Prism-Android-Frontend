/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.changeroommemberroles.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.rolesandpermissions.api.ChangeRoomMemberRolesEntryPoint
import io.prism.android.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import io.prism.android.libraries.matrix.api.room.JoinedRoom
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeChangeRoomMemberRolesEntryPoint : ChangeRoomMemberRolesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node {
        lambdaError()
    }
}
