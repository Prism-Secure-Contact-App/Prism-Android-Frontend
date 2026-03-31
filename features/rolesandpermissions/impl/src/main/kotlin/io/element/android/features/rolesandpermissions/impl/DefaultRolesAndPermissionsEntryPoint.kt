/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.rolesandpermissions.api.RolesAndPermissionsEntryPoint
import io.prism.android.libraries.architecture.createNode
import io.prism.android.libraries.di.RoomScope

@ContributesBinding(RoomScope::class)
class DefaultRolesAndPermissionsEntryPoint : RolesAndPermissionsEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: RolesAndPermissionsEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<RolesAndPermissionsFlowNode>(buildContext, listOf(callback))
    }
}
