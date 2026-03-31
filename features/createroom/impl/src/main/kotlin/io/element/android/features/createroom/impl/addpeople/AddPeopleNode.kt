/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.impl.addpeople

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.features.invitepeople.api.InvitePeoplePresenter
import io.prism.android.features.invitepeople.api.InvitePeopleRenderer
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.core.RoomId

@ContributesNode(SessionScope::class)
@AssistedInject
class AddPeopleNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    invitePeoplePresenterFactory: InvitePeoplePresenter.Factory,
    private val invitePeopleRenderer: InvitePeopleRenderer,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val roomId: RoomId,
    ) : NodeInputs

    interface Callback : Plugin {
        fun onFinish()
    }

    private val callback: Callback = callback()
    private val roomId = inputs<Inputs>().roomId
    private val invitePeoplePresenter = invitePeoplePresenterFactory.create(
        joinedRoom = null,
        roomId = roomId,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = invitePeoplePresenter.present()
        AddPeopleView(
            state = state,
            onFinish = callback::onFinish,
        ) {
            invitePeopleRenderer.Render(state, Modifier)
        }
    }
}
