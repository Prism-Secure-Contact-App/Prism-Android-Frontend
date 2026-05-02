/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.share

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.location.impl.common.actions.FakeLocationActions
import io.prism.android.features.location.impl.common.permissions.FakePermissionsPresenter
import io.prism.android.features.messages.test.FakeMessageComposerContext
import io.prism.android.libraries.dateformatter.test.FakeDurationFormatter
import io.prism.android.libraries.featureflag.test.FakeFeatureFlagService
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.core.aBuildMeta
import io.prism.android.libraries.matrix.test.room.FakeJoinedRoom
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.tests.testutils.node.TestParentNode
import org.junit.Rule
import org.junit.Test

class DefaultShareLocationEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `test node builder`() {
        val entryPoint = DefaultShareLocationEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            ShareLocationNode(
                buildContext = buildContext,
                plugins = plugins,
                presenterFactory = { timelineMode: Timeline.Mode ->
                    ShareLocationPresenter(
                        permissionsPresenterFactory = { FakePermissionsPresenter() },
                        room = FakeJoinedRoom(),
                        timelineMode = timelineMode,
                        analyticsService = FakeAnalyticsService(),
                        messageComposerContext = FakeMessageComposerContext(),
                        locationActions = FakeLocationActions(),
                        buildMeta = aBuildMeta(),
                        featureFlagService = FakeFeatureFlagService(),
                        client = FakePRISMClient(),
                        durationFormatter = FakeDurationFormatter(),
                    )
                },
                analyticsService = FakeAnalyticsService(),
            )
        }
        val timelineMode = Timeline.Mode.Live
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            timelineMode = timelineMode,
        )
        assertThat(result).isInstanceOf(ShareLocationNode::class.java)
        assertThat(result.plugins).contains(ShareLocationNode.Inputs(timelineMode))
    }
}
