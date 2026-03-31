/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.prism.android.features.lightning.api.LightningEntryPoint
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
@Inject
class LightningEntryPointImpl(
    private val factory: LightningNode.Factory,
) : LightningEntryPoint {

    override fun createNode(buildContext: BuildContext, onBack: () -> Unit): Node {
        return factory.create(
            buildContext = buildContext,
            onBack = onBack,
        )
    }
}
