/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.JoinedRoom

fun interface ChangeRoomMemberRolesEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node

    interface NodeProxy {
        val roomId: RoomId
        suspend fun waitForCompletion(): Boolean
    }
}

enum class ChangeRoomMemberRolesListType {
    SelectNewOwnersWhenLeaving,
    Admins,
    Moderators
}
