/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.call.test.FakePRISMCallEntryPoint
import io.prism.android.features.forward.test.FakeForwardEntryPoint
import io.prism.android.features.knockrequests.test.FakeKnockRequestsListEntryPoint
import io.prism.android.features.location.test.FakeLocationService
import io.prism.android.features.location.test.FakeShareLocationEntryPoint
import io.prism.android.features.location.test.FakeShowLocationEntryPoint
import io.prism.android.features.messages.api.MessagesEntryPoint
import io.prism.android.features.messages.impl.pinned.banner.createPinnedEventsTimelineProvider
import io.prism.android.features.messages.impl.timeline.createTimelineController
import io.prism.android.features.poll.test.create.FakeCreatePollEntryPoint
import io.prism.android.libraries.dateformatter.test.FakeDateFormatter
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.permalink.PermalinkData
import io.prism.android.libraries.prism.test.AN_EVENT_ID
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.roomlist.FakeRoomListService
import io.prism.android.libraries.prism.ui.messages.RoomMemberProfilesCache
import io.prism.android.libraries.prism.ui.messages.RoomNamesCache
import io.prism.android.libraries.mediaviewer.test.FakeMediaViewerEntryPoint
import io.prism.android.libraries.textcomposer.mentions.MentionSpanTheme
import io.prism.android.libraries.textcomposer.mentions.MentionSpanUpdater
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.node.TestParentNode
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DefaultMessagesEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test node builder`() = runTest {
        val entryPoint = DefaultMessagesEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            MessagesFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                roomListService = FakeRoomListService(),
                sessionId = A_SESSION_ID,
                shareLocationEntryPoint = FakeShareLocationEntryPoint(),
                showLocationEntryPoint = FakeShowLocationEntryPoint(),
                createPollEntryPoint = FakeCreatePollEntryPoint(),
                prismCallEntryPoint = FakePRISMCallEntryPoint(),
                mediaViewerEntryPoint = FakeMediaViewerEntryPoint(),
                forwardEntryPoint = FakeForwardEntryPoint(),
                analyticsService = FakeAnalyticsService(),
                locationService = FakeLocationService(),
                room = FakeBaseRoom(),
                roomMemberProfilesCache = RoomMemberProfilesCache(),
                roomNamesCache = RoomNamesCache(),
                mentionSpanUpdater = object : MentionSpanUpdater {
                    override fun updateMentionSpans(text: CharSequence) = text

                    @Composable
                    override fun rememberMentionSpans(text: CharSequence) = text
                },
                mentionSpanTheme = MentionSpanTheme(A_USER_ID),
                pinnedEventsTimelineProvider = createPinnedEventsTimelineProvider(),
                timelineController = createTimelineController(),
                knockRequestsListEntryPoint = FakeKnockRequestsListEntryPoint(),
                dateFormatter = FakeDateFormatter(),
                coroutineDispatchers = testCoroutineDispatchers(),
            )
        }
        val callback = object : MessagesEntryPoint.Callback {
            override fun navigateToRoomDetails() = lambdaError()
            override fun navigateToRoomMemberDetails(userId: UserId) = lambdaError()
            override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) = lambdaError()
            override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) = lambdaError()
            override fun navigateToRoom(roomId: RoomId) = lambdaError()
        }
        val initialTarget = MessagesEntryPoint.InitialTarget.Messages(focusedEventId = AN_EVENT_ID)
        val params = MessagesEntryPoint.Params(initialTarget)
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            params = params,
            callback = callback,
        )
        assertThat(result).isInstanceOf(MessagesFlowNode::class.java)
        assertThat(result.plugins).contains(MessagesEntryPoint.Params(initialTarget))
        assertThat(result.plugins).contains(callback)
    }

    @Test
    fun `test initial target to nav target mapping`() {
        assertThat(MessagesEntryPoint.InitialTarget.Messages(focusedEventId = AN_EVENT_ID).toNavTarget())
            .isEqualTo(MessagesFlowNode.NavTarget.Messages(focusedEventId = AN_EVENT_ID))
        assertThat(MessagesEntryPoint.InitialTarget.PinnedMessages.toNavTarget())
            .isEqualTo(MessagesFlowNode.NavTarget.PinnedMessagesList)
    }
}
