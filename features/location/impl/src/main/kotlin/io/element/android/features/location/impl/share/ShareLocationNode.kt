/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.fathertkt.prism.features.analytics.plan.MobileScreen
import io.prism.android.annotations.ContributesNode
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.timeline.Timeline
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesNode(RoomScope::class)
@AssistedInject
class ShareLocationNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ShareLocationPresenter.Factory,
    analyticsService: AnalyticsService,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val timelineMode: Timeline.Mode,
    ) : NodeInputs

    private val presenter = presenterFactory.create(inputs<Inputs>().timelineMode)

    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.LocationSend))
            }
        )
    }

    @Composable
    override fun View(modifier: Modifier) {
        ShareLocationView(
            state = presenter.present(),
            modifier = modifier,
            navigateUp = ::navigateUp,
        )
    }
}
