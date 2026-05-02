/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.roomselect.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.appyx.launchMolecule
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.roomselect.api.RoomSelectEntryPoint
import io.prism.android.libraries.roomselect.api.RoomSelectMode

@ContributesNode(SessionScope::class)
@AssistedInject
class RoomSelectNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: RoomSelectPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val mode: RoomSelectMode,
    ) : NodeInputs

    private val inputs: Inputs = inputs()
    private val presenter = presenterFactory.create(inputs.mode)
    private val stateFlow = launchMolecule { presenter.present() }
    private val callback: RoomSelectEntryPoint.Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state by stateFlow.collectAsState()
        RoomSelectView(
            state = state,
            onDismiss = callback::onCancel,
            onSubmit = callback::onRoomSelected,
            modifier = modifier
        )
    }
}
