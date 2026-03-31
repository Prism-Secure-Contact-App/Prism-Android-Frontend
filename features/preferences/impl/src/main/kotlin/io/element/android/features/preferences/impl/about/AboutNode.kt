/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.about

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
class AboutNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: AboutPresenter,
) : Node(buildContext, plugins = plugins) {
    interface Callback : Plugin {
        fun navigateToOssLicenses()
    }

    private val callback: Callback = callback()

    private fun onPRISMLegalClick(
        activity: Activity,
        darkTheme: Boolean,
        prismLegal: PRISMLegal,
    ) {
        activity.openUrlInChromeCustomTab(null, darkTheme, prismLegal.url)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = PRISMTheme.isLightTheme.not()
        val state = presenter.present()
        AboutView(
            state = state,
            onBackClick = ::navigateUp,
            onPRISMLegalClick = { prismLegal ->
                onPRISMLegalClick(activity, isDark, prismLegal)
            },
            onOpenSourceLicensesClick = callback::navigateToOssLicenses,
            modifier = modifier
        )
    }
}
