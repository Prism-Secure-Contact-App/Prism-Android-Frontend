/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.linknewdevice.api.LinkNewDeviceEntryPoint
import io.prism.android.libraries.architecture.createNode
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultLinkNewDeviceEntryPoint : LinkNewDeviceEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: LinkNewDeviceEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<LinkNewDeviceFlowNode>(
            buildContext = buildContext,
            plugins = listOf(
                callback,
            )
        )
    }
}
