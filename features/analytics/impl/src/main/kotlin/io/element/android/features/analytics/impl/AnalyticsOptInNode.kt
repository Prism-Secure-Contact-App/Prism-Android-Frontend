/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.analytics.impl

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.appconfig.AnalyticsConfig
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.androidutils.browser.openUrlInChromeCustomTab

@ContributesNode(AppScope::class)
@AssistedInject
class AnalyticsOptInNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: AnalyticsOptInPresenter,
) : Node(buildContext, plugins = plugins) {
    private fun onClickTerms(activity: Activity, darkTheme: Boolean) {
        activity.openUrlInChromeCustomTab(null, darkTheme, AnalyticsConfig.POLICY_LINK)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = PRISMTheme.isLightTheme.not()
        val state = presenter.present()
        AnalyticsOptInView(
            state = state,
            modifier = modifier,
            onClickTerms = { onClickTerms(activity, isDark) },
        )
    }
}
