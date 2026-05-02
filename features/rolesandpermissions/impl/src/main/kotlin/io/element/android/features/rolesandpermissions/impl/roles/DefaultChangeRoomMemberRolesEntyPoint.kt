/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.roles

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.rolesandpermissions.api.ChangeRoomMemberRolesEntryPoint
import io.prism.android.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import io.prism.android.libraries.architecture.createNode
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.room.JoinedRoom

@ContributesBinding(SessionScope::class)
class DefaultChangeRoomMemberRolesEntyPoint : ChangeRoomMemberRolesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node {
        return parentNode.createNode<ChangeRoomMemberRolesRootNode>(
            buildContext = buildContext,
            plugins = listOf(
                ChangeRoomMemberRolesRootNode.Inputs(joinedRoom = room, listType = listType),
            )
        )
    }
}
