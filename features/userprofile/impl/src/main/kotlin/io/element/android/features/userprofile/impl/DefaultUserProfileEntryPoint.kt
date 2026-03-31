/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.userprofile.api.UserProfileEntryPoint
import io.prism.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultUserProfileEntryPoint : UserProfileEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: UserProfileEntryPoint.Params,
        callback: UserProfileEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<UserProfileFlowNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback),
        )
    }
}
