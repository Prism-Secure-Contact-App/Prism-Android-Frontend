/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.matrix.api.core.RoomId

interface SpaceEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: Inputs,
        callback: Callback
    ): Node

    data class Inputs(
        val roomId: RoomId
    ) : NodeInputs

    interface Callback : Plugin {
        fun navigateToRoom(roomId: RoomId, viaParameters: List<String>)
        fun navigateToRoomMemberList()
    }
}
