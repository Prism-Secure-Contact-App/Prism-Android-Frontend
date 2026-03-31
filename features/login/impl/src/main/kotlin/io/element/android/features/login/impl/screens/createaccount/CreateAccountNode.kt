/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

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
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.inputs

@ContributesNode(AppScope::class)
@AssistedInject
class CreateAccountNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: CreateAccountPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val url: String,
    ) : NodeInputs

    private val presenter = presenterFactory.create(inputs<Inputs>().url)

    private fun onOpenExternalUrl(activity: Activity, darkTheme: Boolean, url: String) {
        activity.openUrlInChromeCustomTab(null, darkTheme, url)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = PRISMTheme.isLightTheme.not()
        val state = presenter.present()
        CreateAccountView(
            state = state,
            modifier = modifier,
            onBackClick = ::navigateUp,
            onOpenExternalUrl = {
                onOpenExternalUrl(activity, isDark, it)
            },
        )
    }
}
