/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.prism.android.features.vault.api.VaultEntryPoint
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
@Inject
class VaultEntryPointImpl(
    private val factory: VaultNode.Factory,
) : VaultEntryPoint {

    override fun createNode(buildContext: BuildContext, onBack: () -> Unit): Node {
        return factory.create(
            buildContext = buildContext,
            onBack = onBack,
            onRoomClick = { /* Room click from vault navigates to that room — wired via HomeFlowNode */ },
        )
    }
}
