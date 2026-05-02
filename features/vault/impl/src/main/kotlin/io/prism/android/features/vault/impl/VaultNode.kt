/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.vault.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory

class VaultNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onRoomClick: (roomId: String) -> Unit,
    private val presenter: VaultPresenter,
) : Node(buildContext) {

    @AssistedFactory
    interface Factory {
        fun create(
            buildContext: BuildContext,
            onBack: () -> Unit,
            onRoomClick: (roomId: String) -> Unit,
        ): VaultNode
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        VaultView(
            state = state,
            onBack = onBack,
            onRoomClick = onRoomClick,
            modifier = modifier,
        )
    }
}
