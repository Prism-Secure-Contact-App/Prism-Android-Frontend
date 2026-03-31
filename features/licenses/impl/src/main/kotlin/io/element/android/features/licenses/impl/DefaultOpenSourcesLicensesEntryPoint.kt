/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.licenses.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.licenses.api.OpenSourceLicensesEntryPoint
import io.prism.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultOpenSourcesLicensesEntryPoint : OpenSourceLicensesEntryPoint {
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        return parentNode.createNode<DependenciesFlowNode>(buildContext)
    }
}
