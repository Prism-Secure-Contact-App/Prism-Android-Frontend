/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.pinned.list

import com.google.common.truth.Truth.assertThat
import im.vector.app.features.analytics.plan.PinUnpinAction
import io.prism.android.features.messages.impl.actionlist.anActionListState
import io.prism.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.prism.android.features.messages.impl.fixtures.aTimelineItemsFactoryCreator
import io.prism.android.features.messages.impl.link.aLinkState
import io.prism.android.features.messages.impl.pinned.DefaultPinnedEventsTimelineProvider
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.protection.aTimelineProtectionState
import io.prism.android.features.messages.test.timeline.FakeHtmlConverterProvider
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.prism.android.libraries.featureflag.test.FakeFeatureFlagService
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.room.JoinedRoom
import io.prism.android.libraries.matrix.api.sync.SyncService
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.prism.android.libraries.matrix.test.AN_EVENT_ID
import io.prism.android.libraries.matrix.test.AN_EXCEPTION
import io.prism.android.libraries.matrix.test.A_UNIQUE_ID
import io.prism.android.libraries.matrix.test.room.FakeBaseRoom
import io.prism.android.libraries.matrix.test.room.FakeJoinedRoom
import io.prism.android.libraries.matrix.test.room.aRoomInfo
import io.prism.android.libraries.matrix.test.room.powerlevels.FakeRoomPermissions
import io.prism.android.libraries.matrix.test.sync.FakeSyncService
import io.prism.android.libraries.matrix.test.timeline.FakeTimeline
import io.prism.android.libraries.matrix.test.timeline.aMessageContent
import io.prism.android.libraries.matrix.test.timeline.anEventTimelineItem
import io.prism.android.services.analytics.api.AnalyticsService
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinnedMessagesListPresenterTest {
    @Test
    fun `present - initial state`() = runTest {
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            }
        )
        val presenter = createPinnedMessagesListPresenter(room = room)
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(PinnedMessagesListState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - timeline failure state`() = runTest {
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.failure(RuntimeException()) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room)
        presenter.test {
            skipItems(3)
            val failureState = awaitItem()
            assertThat(failureState).isEqualTo(PinnedMessagesListState.Failed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - empty state`() = runTest {
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf()))
            },
            createTimelineResult = { Result.success(FakeTimeline()) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room)
        presenter.test {
            skipItems(3)
            val emptyState = awaitItem()
            assertThat(emptyState).isEqualTo(PinnedMessagesListState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - filled state`() = runTest {
        val pinnedEventsTimeline = createPinnedMessagesTimeline()
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.success(pinnedEventsTimeline) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room)
        presenter.test {
            skipItems(3)
            val filledState = awaitItem() as PinnedMessagesListState.Filled
            assertThat(filledState.timelineItems).hasSize(1)
            assertThat(filledState.loadedPinnedMessagesCount).isEqualTo(1)
            assertThat(filledState.userEventPermissions.canRedactOwn).isTrue()
            assertThat(filledState.userEventPermissions.canRedactOther).isTrue()
            assertThat(filledState.userEventPermissions.canPinUnpin).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - unpin event`() = runTest {
        val successUnpinEventLambda = lambdaRecorder { _: EventId? -> Result.success(true) }
        val failureUnpinEventLambda = lambdaRecorder { _: EventId? -> Result.failure<Boolean>(AN_EXCEPTION) }
        val pinnedEventsTimeline = createPinnedMessagesTimeline()
        val analyticsService = FakeAnalyticsService()
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.success(pinnedEventsTimeline) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room, analyticsService = analyticsService)
        presenter.test {
            skipItems(3)
            val filledState = awaitItem() as PinnedMessagesListState.Filled
            val eventItem = filledState.timelineItems.first() as TimelineItem.Event

            pinnedEventsTimeline.unpinEventLambda = successUnpinEventLambda
            filledState.eventSink(PinnedMessagesListEvent.HandleAction(TimelineItemAction.Unpin, eventItem))
            advanceUntilIdle()

            pinnedEventsTimeline.unpinEventLambda = failureUnpinEventLambda
            filledState.eventSink(PinnedMessagesListEvent.HandleAction(TimelineItemAction.Unpin, eventItem))
            advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()

            assert(successUnpinEventLambda)
                .isCalledOnce()
                .with(value(AN_EVENT_ID))

            assert(failureUnpinEventLambda)
                .isCalledOnce()
                .with(value(AN_EVENT_ID))

            assertThat(analyticsService.capturedEvents).containsExactly(
                PinUnpinAction(kind = PinUnpinAction.Kind.Unpin, from = PinUnpinAction.From.MessagePinningList),
                PinUnpinAction(kind = PinUnpinAction.Kind.Unpin, from = PinUnpinAction.From.MessagePinningList)
            )
        }
    }

    @Test
    fun `present - navigate to event`() = runTest {
        val onViewInTimelineClickLambda = lambdaRecorder { _: EventId -> }
        val navigator = FakePinnedMessagesListNavigator().apply {
            this.onViewInTimelineClickLambda = onViewInTimelineClickLambda
        }
        val pinnedEventsTimeline = createPinnedMessagesTimeline()
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.success(pinnedEventsTimeline) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room, navigator = navigator)
        presenter.test {
            skipItems(3)
            val filledState = awaitItem() as PinnedMessagesListState.Filled
            val eventItem = filledState.timelineItems.first() as TimelineItem.Event
            filledState.eventSink(PinnedMessagesListEvent.HandleAction(TimelineItemAction.ViewInTimeline, eventItem))
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            assert(onViewInTimelineClickLambda)
                .isCalledOnce()
                .with(value(AN_EVENT_ID))
        }
    }

    @Test
    fun `present - show view source action`() = runTest {
        val onShowEventDebugInfoClickLambda = lambdaRecorder { _: EventId?, _: TimelineItemDebugInfo -> }
        val navigator = FakePinnedMessagesListNavigator().apply {
            this.onShowEventDebugInfoClickLambda = onShowEventDebugInfoClickLambda
        }
        val pinnedEventsTimeline = createPinnedMessagesTimeline()
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.success(pinnedEventsTimeline) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room, navigator = navigator)
        presenter.test {
            skipItems(3)
            val filledState = awaitItem() as PinnedMessagesListState.Filled
            val eventItem = filledState.timelineItems.first() as TimelineItem.Event
            filledState.eventSink(PinnedMessagesListEvent.HandleAction(TimelineItemAction.ViewSource, eventItem))
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            assert(onShowEventDebugInfoClickLambda)
                .isCalledOnce()
                .with(value(AN_EVENT_ID), value(eventItem.debugInfo))
        }
    }

    @Test
    fun `present - forward event`() = runTest {
        val onForwardEventClickLambda = lambdaRecorder { _: EventId -> }
        val navigator = FakePinnedMessagesListNavigator().apply {
            this.onForwardEventClickLambda = onForwardEventClickLambda
        }
        val pinnedEventsTimeline = createPinnedMessagesTimeline()
        val room = FakeJoinedRoom(
            baseRoom = FakeBaseRoom(
                roomPermissions = roomPermissions(),
            ).apply {
                givenRoomInfo(aRoomInfo(pinnedEventIds = listOf(AN_EVENT_ID)))
            },
            createTimelineResult = { Result.success(pinnedEventsTimeline) },
        )
        val presenter = createPinnedMessagesListPresenter(room = room, navigator = navigator)
        presenter.test {
            skipItems(3)
            val filledState = awaitItem() as PinnedMessagesListState.Filled
            val eventItem = filledState.timelineItems.first() as TimelineItem.Event
            filledState.eventSink(PinnedMessagesListEvent.HandleAction(TimelineItemAction.Forward, eventItem))
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            assert(onForwardEventClickLambda)
                .isCalledOnce()
                .with(value(AN_EVENT_ID))
        }
    }

    private fun createPinnedMessagesTimeline(): FakeTimeline {
        val messageContent = aMessageContent("A message")
        return FakeTimeline(
            timelineItems = flowOf(
                listOf(
                    PRISMTimelineItem.Event(
                        uniqueId = A_UNIQUE_ID,
                        event = anEventTimelineItem(
                            eventId = AN_EVENT_ID,
                            content = messageContent,
                        ),
                    )
                )
            )
        )
    }

    private fun roomPermissions(
        canRedactOther: Boolean = true,
        canRedactOwn: Boolean = true,
        canPinUnpin: Boolean = true,
    ) = FakeRoomPermissions(
        canRedactOther = canRedactOther,
        canRedactOwn = canRedactOwn,
        canPinUnpin = canPinUnpin,
    )

    private fun TestScope.createPinnedMessagesListPresenter(
        navigator: PinnedMessagesListNavigator = FakePinnedMessagesListNavigator(),
        room: JoinedRoom = FakeJoinedRoom(),
        syncService: SyncService = FakeSyncService(),
        analyticsService: AnalyticsService = FakeAnalyticsService(),
        featureFlagService: FakeFeatureFlagService = FakeFeatureFlagService(),
    ): PinnedMessagesListPresenter {
        val timelineProvider = DefaultPinnedEventsTimelineProvider(
            room = room,
            syncService = syncService,
            dispatchers = testCoroutineDispatchers(),
        )
        timelineProvider.launchIn(backgroundScope)
        return PinnedMessagesListPresenter(
            navigator = navigator,
            room = room,
            timelineItemsFactoryCreator = aTimelineItemsFactoryCreator(),
            timelineProvider = timelineProvider,
            timelineProtectionPresenter = { aTimelineProtectionState() },
            snackbarDispatcher = SnackbarDispatcher(),
            actionListPresenter = { anActionListState() },
            linkPresenter = { aLinkState() },
            analyticsService = analyticsService,
            featureFlagService = featureFlagService,
            sessionCoroutineScope = this,
            htmlConverterProvider = FakeHtmlConverterProvider(),
        )
    }
}
