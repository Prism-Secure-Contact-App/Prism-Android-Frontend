/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.forward.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.forward.api.ForwardEntryPoint
import io.prism.android.libraries.architecture.createNode
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultForwardEntryPoint : ForwardEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: ForwardEntryPoint.Params,
        callback: ForwardEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<ForwardMessagesNode>(
            buildContext = buildContext,
            plugins = listOf(
                ForwardMessagesNode.Inputs(
                    eventId = params.eventId,
                    timelineProvider = params.timelineProvider,
                ),
                callback,
            )
        )
    }
}
