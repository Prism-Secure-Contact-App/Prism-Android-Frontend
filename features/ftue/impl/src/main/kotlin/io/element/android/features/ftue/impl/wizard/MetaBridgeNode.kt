/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.ftue.impl.state.DefaultFtueService
import io.prism.android.libraries.matrix.api.PRISMClient

class MetaBridgeNode(
    buildContext: BuildContext,
    private val ftueService: DefaultFtueService,
    private val matrixClient: PRISMClient,
    private val onBack: () -> Unit,
) : Node(buildContext) {

    private val presenter = BridgePresenter(
        matrixClient = matrixClient,
        flow = MetaBridgeFlow(),
        onConnected = { ftueService.completeCurrentStepAndAdvance() },
        onSkip = { ftueService.completeCurrentStepAndAdvance() },
        onBack = onBack,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        MetaBridgeView(
            state = state,
            modifier = modifier,
        )
    }
}
