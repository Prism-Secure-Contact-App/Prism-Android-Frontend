/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
class LinkNewDeviceRootNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: LinkNewDeviceRootPresenter,
) : Node(buildContext, plugins = plugins) {
    interface Callback : Plugin {
        fun onDone()
        fun linkDesktopDevice()
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        LinkNewDeviceRootView(
            state = state,
            modifier = modifier,
            onBackClick = callback::onDone,
            onLinkDesktopDeviceClick = callback::linkDesktopDevice,
        )
    }
}
