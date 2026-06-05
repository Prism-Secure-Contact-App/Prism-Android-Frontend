/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.core.RoomId

@ContributesNode(SessionScope::class)
class PrismAISpaceNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<com.bumble.appyx.core.plugin.Plugin>,
    private val presenter: PrismAISpacePresenter,
) : Node(buildContext, plugins = plugins) {

    interface Callback : com.bumble.appyx.core.plugin.Plugin {
        fun onNavigateToRoom(roomId: RoomId)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val callback = plugins.filterIsInstance<Callback>().firstOrNull()
        val state = presenter.present()
        PrismAISpaceView(
            state = state,
            onRoomClick = { roomId ->
                callback?.onNavigateToRoom(RoomId(roomId))
            },
            onBackClick = ::navigateUp,
            modifier = modifier,
        )
    }
}
