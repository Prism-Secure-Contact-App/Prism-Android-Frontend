/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.loggedin

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.sync.SyncState
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.room.FakeJoinedRoom
import io.prism.android.libraries.matrix.test.sync.FakeSyncService
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SendQueuesTest {
    private val matrixClient = FakePRISMClient()
    private val syncService = FakeSyncService(initialSyncState = SyncState.Running)
    private val sut = SendQueues(matrixClient, syncService)

    @Test
    fun `test network status online and sending queue failed`() = runTest {
        val sendQueueDisabledFlow = MutableSharedFlow<RoomId>(replay = 1)
        val setAllSendQueuesEnabledLambda = lambdaRecorder { _: Boolean -> }
        matrixClient.sendQueueDisabledFlow = sendQueueDisabledFlow
        matrixClient.setAllSendQueuesEnabledLambda = setAllSendQueuesEnabledLambda
        val setRoomSendQueueEnabledLambda = lambdaRecorder { _: Boolean -> }
        val room = FakeJoinedRoom(
            setSendQueueEnabledResult = setRoomSendQueueEnabledLambda
        )
        matrixClient.givenGetRoomResult(room.roomId, room)
        sut.launchIn(backgroundScope)

        sendQueueDisabledFlow.emit(room.roomId)
        advanceTimeBy(SEND_QUEUES_RETRY_DELAY_MILLIS)
        runCurrent()

        assert(setAllSendQueuesEnabledLambda)
            .isCalledOnce()
            .with(value(true))

        assert(setRoomSendQueueEnabledLambda).isNeverCalled()
    }

    @Test
    fun `test sync state offline and sending queue failed`() = runTest {
        val sendQueueDisabledFlow = MutableSharedFlow<RoomId>(replay = 1)

        val setAllSendQueuesEnabledLambda = lambdaRecorder { _: Boolean -> }
        matrixClient.sendQueueDisabledFlow = sendQueueDisabledFlow
        matrixClient.setAllSendQueuesEnabledLambda = setAllSendQueuesEnabledLambda
        syncService.emitSyncState(SyncState.Offline)
        val setRoomSendQueueEnabledLambda = lambdaRecorder { _: Boolean -> }
        val room = FakeJoinedRoom(
            setSendQueueEnabledResult = setRoomSendQueueEnabledLambda
        )
        matrixClient.givenGetRoomResult(room.roomId, room)

        sut.launchIn(backgroundScope)

        sendQueueDisabledFlow.emit(room.roomId)
        advanceTimeBy(SEND_QUEUES_RETRY_DELAY_MILLIS)
        runCurrent()

        assert(setAllSendQueuesEnabledLambda).isNeverCalled()
        assert(setRoomSendQueueEnabledLambda).isNeverCalled()
    }
}
