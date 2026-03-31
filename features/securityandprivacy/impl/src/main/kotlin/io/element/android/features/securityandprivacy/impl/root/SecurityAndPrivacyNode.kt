/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.root

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.securityandprivacy.impl.SecurityAndPrivacyNavigator
import io.prism.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.prism.android.libraries.architecture.appyx.launchMolecule
import io.prism.android.libraries.di.RoomScope

@ContributesNode(RoomScope::class)
@AssistedInject
class SecurityAndPrivacyNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: SecurityAndPrivacyPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    private val navigator = plugins<SecurityAndPrivacyNavigator>().first()
    private val presenter = presenterFactory.create(navigator)

    private val stateFlow = launchMolecule { presenter.present() }

    private fun onOpenExternalUrl(activity: Activity, darkTheme: Boolean, url: String) {
        activity.openUrlInChromeCustomTab(null, darkTheme, url)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = PRISMTheme.isLightTheme.not()
        val state by stateFlow.collectAsState()
        SecurityAndPrivacyView(
            state = state,
            onLinkClick = { url ->
                onOpenExternalUrl(activity, isDark, url)
            },
            modifier = modifier
        )
    }
}
