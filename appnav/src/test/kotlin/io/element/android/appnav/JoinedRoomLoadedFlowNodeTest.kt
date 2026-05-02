/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.helper.parentNodeTestHelper
import com.google.common.truth.Truth.assertThat
import io.prism.android.appnav.di.RoomGraphFactory
import io.prism.android.appnav.di.TimelineBindings
import io.prism.android.appnav.room.RoomNavigationTarget
import io.prism.android.appnav.room.joined.FakeJoinedRoomLoadedFlowNodeCallback
import io.prism.android.appnav.room.joined.JoinedRoomLoadedFlowNode
import io.prism.android.features.forward.api.ForwardEntryPoint
import io.prism.android.features.forward.test.FakeForwardEntryPoint
import io.prism.android.features.messages.api.MessagesEntryPoint
import io.prism.android.features.messages.api.pinned.PinnedEventsTimelineProvider
import io.prism.android.features.messages.test.pinned.FakePinnedEventsTimelineProvider
import io.prism.android.features.roomdetails.api.RoomDetailsEntryPoint
import io.prism.android.features.space.api.SpaceEntryPoint
import io.prism.android.libraries.architecture.childNode
import io.prism.android.libraries.matrix.api.room.JoinedRoom
import io.prism.android.libraries.matrix.api.timeline.TimelineProvider
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.room.FakeBaseRoom
import io.prism.android.libraries.matrix.test.room.FakeJoinedRoom
import io.prism.android.libraries.matrix.test.room.aRoomInfo
import io.prism.android.libraries.matrix.test.timeline.FakeTimelineProvider
import io.prism.android.services.analytics.api.watchers.AnalyticsSendMessageWatcher
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.services.analytics.test.watchers.FakeAnalyticsSendMessageWatcher
import io.prism.android.services.appnavstate.api.ActiveRoomsHolder
import io.prism.android.services.appnavstate.impl.DefaultActiveRoomsHolder
import io.prism.android.services.appnavstate.test.FakeAppNavigationStateService
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JoinedRoomLoadedFlowNodeTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeMessagesEntryPoint : MessagesEntryPoint {
        var nodeId: String? = null
        var parameters: MessagesEntryPoint.Params? = null
        var callback: MessagesEntryPoint.Callback? = null

        override fun createNode(
            parentNode: Node,
            buildContext: BuildContext,
            params: MessagesEntryPoint.Params,
            callback: MessagesEntryPoint.Callback,
        ): Node {
            parameters = params
            this.callback = callback
            return node(buildContext) {}.also {
                nodeId = it.id
            }
        }
    }

    private class FakeRoomGraphFactory(
        private val timelineProvider: FakeTimelineProvider = FakeTimelineProvider(),
        private val pinnedEventsTimelineProvider: FakePinnedEventsTimelineProvider = FakePinnedEventsTimelineProvider(),
        private val analyticsSendMessageWatcher: FakeAnalyticsSendMessageWatcher = FakeAnalyticsSendMessageWatcher(),
    ) : RoomGraphFactory {
        override fun create(room: JoinedRoom): Any {
            return object : TimelineBindings {
                override val timelineProvider: TimelineProvider
                    get() = this@FakeRoomGraphFactory.timelineProvider
                override val pinnedEventsTimelineProvider: PinnedEventsTimelineProvider
                    get() = this@FakeRoomGraphFactory.pinnedEventsTimelineProvider
                override val analyticsSendMessageWatcher: AnalyticsSendMessageWatcher
                    get() = this@FakeRoomGraphFactory.analyticsSendMessageWatcher
            }
        }
    }

    private class FakeRoomDetailsEntryPoint : RoomDetailsEntryPoint {
        var nodeId: String? = null

        override fun createNode(
            parentNode: Node,
            buildContext: BuildContext,
            params: RoomDetailsEntryPoint.Params,
            callback: RoomDetailsEntryPoint.Callback,
        ) = node(buildContext) {}.also {
            nodeId = it.id
        }
    }

    private class FakeSpaceEntryPoint : SpaceEntryPoint {
        var nodeId: String? = null

        override fun createNode(
            parentNode: Node,
            buildContext: BuildContext,
            inputs: SpaceEntryPoint.Inputs,
            callback: SpaceEntryPoint.Callback,
        ) = node(buildContext) {}.also {
            nodeId = it.id
        }
    }

    private fun TestScope.createJoinedRoomLoadedFlowNode(
        plugins: List<Plugin>,
        messagesEntryPoint: MessagesEntryPoint = FakeMessagesEntryPoint(),
        roomDetailsEntryPoint: RoomDetailsEntryPoint = FakeRoomDetailsEntryPoint(),
        spaceEntryPoint: SpaceEntryPoint = FakeSpaceEntryPoint(),
        forwardEntryPoint: ForwardEntryPoint = FakeForwardEntryPoint(),
        activeRoomsHolder: ActiveRoomsHolder = DefaultActiveRoomsHolder(),
        matrixClient: FakePRISMClient = FakePRISMClient(),
    ) = JoinedRoomLoadedFlowNode(
        buildContext = BuildContext.root(savedStateMap = null),
        plugins = plugins,
        messagesEntryPoint = messagesEntryPoint,
        roomDetailsEntryPoint = roomDetailsEntryPoint,
        spaceEntryPoint = spaceEntryPoint,
        forwardEntryPoint = forwardEntryPoint,
        appNavigationStateService = FakeAppNavigationStateService(),
        sessionCoroutineScope = backgroundScope,
        roomGraphFactory = FakeRoomGraphFactory(),
        matrixClient = matrixClient,
        activeRoomsHolder = activeRoomsHolder,
        analyticsService = FakeAnalyticsService(),
    )

    @Test
    fun `given a room flow node when initialized then it loads messages entry point if room is not space`() = runTest {
        // GIVEN
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(updateMembersResult = {}, initialRoomInfo = aRoomInfo(isSpace = false)))
        val fakeMessagesEntryPoint = FakeMessagesEntryPoint()
        val inputs = JoinedRoomLoadedFlowNode.Inputs(room, RoomNavigationTarget.Root())
        val roomFlowNode = createJoinedRoomLoadedFlowNode(
            plugins = listOf(inputs, FakeJoinedRoomLoadedFlowNodeCallback()),
            messagesEntryPoint = fakeMessagesEntryPoint,
        )
        // WHEN
        val roomFlowNodeTestHelper = roomFlowNode.parentNodeTestHelper()

        // THEN
        assertThat(roomFlowNode.backstack.activeElement).isEqualTo(JoinedRoomLoadedFlowNode.NavTarget.Messages())
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.Messages(), Lifecycle.State.CREATED)
        val messagesNode = roomFlowNode.childNode(JoinedRoomLoadedFlowNode.NavTarget.Messages())!!
        assertThat(messagesNode.id).isEqualTo(fakeMessagesEntryPoint.nodeId)
    }

    @Test
    fun `given a room flow node when initialized then it loads space entry point if room is space`() = runTest {
        // GIVEN
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(updateMembersResult = {}, initialRoomInfo = aRoomInfo(isSpace = true)))
        val spaceEntryPoint = FakeSpaceEntryPoint()
        val inputs = JoinedRoomLoadedFlowNode.Inputs(room, RoomNavigationTarget.Root())
        val roomFlowNode = createJoinedRoomLoadedFlowNode(
            plugins = listOf(inputs, FakeJoinedRoomLoadedFlowNodeCallback()),
            spaceEntryPoint = spaceEntryPoint,
        )
        // WHEN
        val roomFlowNodeTestHelper = roomFlowNode.parentNodeTestHelper()

        // THEN
        assertThat(roomFlowNode.backstack.activeElement).isEqualTo(JoinedRoomLoadedFlowNode.NavTarget.Space)
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.Space, Lifecycle.State.CREATED)
        val spaceNode = roomFlowNode.childNode(JoinedRoomLoadedFlowNode.NavTarget.Space)!!
        assertThat(spaceNode.id).isEqualTo(spaceEntryPoint.nodeId)
    }

    @Test
    fun `given a room flow node when callback on room details is triggered then it loads room details entry point`() = runTest {
        // GIVEN
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(updateMembersResult = {}))
        val fakeMessagesEntryPoint = FakeMessagesEntryPoint()
        val fakeRoomDetailsEntryPoint = FakeRoomDetailsEntryPoint()
        val inputs = JoinedRoomLoadedFlowNode.Inputs(room, RoomNavigationTarget.Root())
        val roomFlowNode = createJoinedRoomLoadedFlowNode(
            plugins = listOf(inputs, FakeJoinedRoomLoadedFlowNodeCallback()),
            messagesEntryPoint = fakeMessagesEntryPoint,
            roomDetailsEntryPoint = fakeRoomDetailsEntryPoint,
        )
        val roomFlowNodeTestHelper = roomFlowNode.parentNodeTestHelper()
        // WHEN
        fakeMessagesEntryPoint.callback?.navigateToRoomDetails()
        // THEN
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.RoomDetails, Lifecycle.State.CREATED)
        val roomDetailsNode = roomFlowNode.childNode(JoinedRoomLoadedFlowNode.NavTarget.RoomDetails)!!
        assertThat(roomDetailsNode.id).isEqualTo(fakeRoomDetailsEntryPoint.nodeId)
    }

    @Test
    fun `the ActiveRoomsHolder will be updated with the loaded room on create`() = runTest {
        // GIVEN
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(updateMembersResult = {}))
        val fakeMessagesEntryPoint = FakeMessagesEntryPoint()
        val fakeRoomDetailsEntryPoint = FakeRoomDetailsEntryPoint()
        val inputs = JoinedRoomLoadedFlowNode.Inputs(room, RoomNavigationTarget.Root())
        val activeRoomsHolder = DefaultActiveRoomsHolder()
        val roomFlowNode = createJoinedRoomLoadedFlowNode(
            plugins = listOf(inputs, FakeJoinedRoomLoadedFlowNodeCallback()),
            messagesEntryPoint = fakeMessagesEntryPoint,
            roomDetailsEntryPoint = fakeRoomDetailsEntryPoint,
            activeRoomsHolder = activeRoomsHolder,
        )

        assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNull()
        val roomFlowNodeTestHelper = roomFlowNode.parentNodeTestHelper()
        // WHEN
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.Messages(null), Lifecycle.State.CREATED)
        // THEN
        assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNotNull()
    }

    @Test
    fun `the ActiveRoomsHolder will be removed on destroy`() = runTest {
        // GIVEN
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(updateMembersResult = {}))
        val fakeMessagesEntryPoint = FakeMessagesEntryPoint()
        val fakeRoomDetailsEntryPoint = FakeRoomDetailsEntryPoint()
        val inputs = JoinedRoomLoadedFlowNode.Inputs(room, RoomNavigationTarget.Root())
        val activeRoomsHolder = DefaultActiveRoomsHolder().apply {
            addRoom(room)
        }
        val roomFlowNode = createJoinedRoomLoadedFlowNode(
            plugins = listOf(inputs, FakeJoinedRoomLoadedFlowNodeCallback()),
            messagesEntryPoint = fakeMessagesEntryPoint,
            roomDetailsEntryPoint = fakeRoomDetailsEntryPoint,
            activeRoomsHolder = activeRoomsHolder,
        )
        val roomFlowNodeTestHelper = roomFlowNode.parentNodeTestHelper()
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.Messages(null), Lifecycle.State.CREATED)
        assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNotNull()
        // WHEN
        roomFlowNode.updateLifecycleState(Lifecycle.State.DESTROYED)
        // THEN
        roomFlowNodeTestHelper.assertChildHasLifecycle(JoinedRoomLoadedFlowNode.NavTarget.Messages(null), Lifecycle.State.DESTROYED)
        assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNull()
    }
}
