/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.accountselect.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.accountselect.api.AccountSelectEntryPoint
import io.prism.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultAccountSelectEntryPoint : AccountSelectEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: AccountSelectEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<AccountSelectNode>(buildContext, listOf(callback))
    }
}
