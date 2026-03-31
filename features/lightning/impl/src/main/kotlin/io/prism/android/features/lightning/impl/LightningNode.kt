/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.lightning.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.assisted.AssistedFactory
import io.prism.android.libraries.architecture.presentNode

class LightningNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted private val onBack: () -> Unit,
    private val presenter: LightningPresenter,
) : Node(buildContext) {

    @AssistedFactory
    interface Factory {
        fun create(
            buildContext: BuildContext,
            onBack: () -> Unit,
        ): LightningNode
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presentNode(presenter)
        LightningView(
            state = state,
            onBack = onBack,
            modifier = modifier,
        )
    }
}
