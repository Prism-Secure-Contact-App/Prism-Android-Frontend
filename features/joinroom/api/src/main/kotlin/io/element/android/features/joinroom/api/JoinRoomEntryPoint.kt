/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import uk.fathertkt.prism.features.analytics.plan.JoinedRoom
import io.prism.android.features.roomdirectory.api.RoomDescription
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.RoomIdOrAlias
import java.util.Optional

interface JoinRoomEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: Inputs,
    ): Node

    data class Inputs(
        val roomId: RoomId,
        val roomIdOrAlias: RoomIdOrAlias,
        val roomDescription: Optional<RoomDescription>,
        val serverNames: List<String>,
        val trigger: JoinedRoom.Trigger,
    ) : NodeInputs
}
