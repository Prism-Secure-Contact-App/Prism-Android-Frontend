/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.messages.api.MessagesEntryPoint
import io.prism.android.libraries.architecture.createNode
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultMessagesEntryPoint : MessagesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: MessagesEntryPoint.Params,
        callback: MessagesEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<MessagesFlowNode>(buildContext, listOf(params, callback))
    }
}

internal fun MessagesEntryPoint.InitialTarget.toNavTarget() = when (this) {
    is MessagesEntryPoint.InitialTarget.Messages -> MessagesFlowNode.NavTarget.Messages(focusedEventId)
    MessagesEntryPoint.InitialTarget.PinnedMessages -> MessagesFlowNode.NavTarget.PinnedMessagesList
}
