/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.leaveroom.impl

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.leaveroom.api.LeaveRoomEvent
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.aRoomInfo
import io.prism.android.libraries.push.test.notifications.conversations.FakeNotificationConversationService
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaveBaseRoomPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state hides all dialogs`() = runTest {
        createLeaveRoomPresenter()
            .stateFlow()
            .test {
                val initialState = awaitItem()
                assertThat(initialState.leaveAction).isEqualTo(AsyncAction.Uninitialized)
            }
    }

    @Test
    fun `present - show generic confirmation`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom().apply {
                        givenRoomInfo(aRoomInfo(isDirect = false, isPublic = true, joinedMembersCount = 10))
                    }
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = true))
            val confirmationState = awaitItem()
            assertThat(confirmationState.leaveAction).isEqualTo(Confirmation.Generic(A_ROOM_ID))
        }
    }

    @Test
    fun `present - show private room confirmation`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom().apply {
                        givenRoomInfo(aRoomInfo(isPublic = false))
                    },
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = true))
            val confirmationState = awaitItem()
            assertThat(confirmationState.leaveAction).isEqualTo(Confirmation.PrivateRoom(A_ROOM_ID))
        }
    }

    @Test
    fun `present - show last user in room confirmation`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom().apply {
                        givenRoomInfo(aRoomInfo(joinedMembersCount = 1))
                    },
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = true))
            val confirmationState = awaitItem()
            assertThat(confirmationState.leaveAction).isEqualTo(Confirmation.LastUserInRoom(A_ROOM_ID))
        }
    }

    @Test
    fun `present - show DM confirmation`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom().apply {
                        givenRoomInfo(aRoomInfo(isDirect = true, activeMembersCount = 2))
                    },
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = true))
            val confirmationState = awaitItem()
            assertThat(confirmationState.leaveAction).isEqualTo(Confirmation.Dm(A_ROOM_ID))
        }
    }

    @Test
    fun `present - leaving a room leaves the room`() = runTest {
        val leaveRoomLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom(
                        leaveRoomLambda = leaveRoomLambda
                    ),
                )
            },
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = false))
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            assert(leaveRoomLambda)
                .isCalledOnce()
                .withNoParameter()
        }
    }

    @Test
    fun `present - show error if leave room fails`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom(
                        leaveRoomLambda = { Result.failure(RuntimeException("Blimey!")) }
                    ),
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = false))
            val progressState = awaitItem()
            assertThat(progressState.leaveAction).isEqualTo(AsyncAction.Loading)
            val errorState = awaitItem()
            assertThat(errorState.leaveAction).isInstanceOf(AsyncAction.Failure::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - reset state after error`() = runTest {
        val presenter = createLeaveRoomPresenter(
            client = FakePRISMClient().apply {
                givenGetRoomResult(
                    roomId = A_ROOM_ID,
                    result = FakeBaseRoom(
                        leaveRoomLambda = { Result.failure(RuntimeException("Blimey!")) }
                    ),
                )
            }
        )
        presenter.stateFlow().test {
            val initialState = awaitItem()
            initialState.eventSink(LeaveRoomEvent.LeaveRoom(A_ROOM_ID, needsConfirmation = false))
            skipItems(1) // Skip show progress state
            val errorState = awaitItem()
            assertThat(errorState.leaveAction).isInstanceOf(AsyncAction.Failure::class.java)
            errorState.eventSink(InternalLeaveRoomEvent.ResetState)
            val hiddenErrorState = awaitItem()
            assertThat(hiddenErrorState.leaveAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    private fun LeaveRoomPresenter.stateFlow(): Flow<InternalLeaveRoomState> {
        return moleculeFlow(RecompositionMode.Immediate) {
            present()
        }.filterIsInstance(InternalLeaveRoomState::class)
    }
}

private fun TestScope.createLeaveRoomPresenter(
    client: PRISMClient = FakePRISMClient(),
): LeaveRoomPresenter = LeaveRoomPresenter(
    client = client,
    dispatchers = testCoroutineDispatchers(false),
    notificationConversationService = FakeNotificationConversationService(),
)
