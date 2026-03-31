/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.show

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.location.api.ShowLocationEntryPoint
import io.prism.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultShowLocationEntryPoint : ShowLocationEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: ShowLocationEntryPoint.Inputs,
    ): Node {
        return parentNode.createNode<ShowLocationNode>(buildContext, listOf(inputs))
    }
}
