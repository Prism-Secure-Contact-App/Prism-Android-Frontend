/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.reportroom.impl

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultReportRoomTest {
    private val roomId = A_ROOM_ID
    private val successLeaveRoomLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
    private val successReportRoomLambda =
        lambdaRecorder<String?, Result<Unit>> { _ -> Result.success(Unit) }

    private val failureLeaveRoomLambda =
        lambdaRecorder<Result<Unit>> { Result.failure(Exception("Leave room error")) }
    private val failureReportRoomLambda =
        lambdaRecorder<String?, Result<Unit>> { _ -> Result.failure(Exception("Report room error")) }

    @Test
    fun `report room, leave=false, report=false, nothing is called`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = false, reason = "", shouldLeave = false)

        assertThat(result.isSuccess).isTrue()
        assert(successLeaveRoomLambda).isNeverCalled()
        assert(successReportRoomLambda).isNeverCalled()
    }

    @Test
    fun `report room, leave=false, report=true, report room success`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = true, reason = "Spam", shouldLeave = false)

        assertThat(result.isSuccess).isTrue()
        assert(successLeaveRoomLambda).isNeverCalled()
        assert(successReportRoomLambda)
            .isCalledOnce()
            .with(value("Spam"))
    }

    @Test
    fun `report room, leave=true, report=false, leave room success`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = false, reason = "", shouldLeave = true)

        assertThat(result.isSuccess).isTrue()
        assert(successLeaveRoomLambda).isCalledOnce()
        assert(successReportRoomLambda).isNeverCalled()
    }

    @Test
    fun `report room, leave=true, report=true, leave room success`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = true, reason = "Spam", shouldLeave = true)

        assertThat(result.isSuccess).isTrue()
        assert(successLeaveRoomLambda).isCalledOnce()
        assert(successReportRoomLambda)
            .isCalledOnce()
            .with(value("Spam"))
    }

    @Test
    fun `report room, leave=true, report=true, leave room failed`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = failureLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = true, reason = "Spam", shouldLeave = true)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(ReportRoom.Exception.LeftRoomFailed)
        assert(failureLeaveRoomLambda).isCalledOnce()
        assert(successReportRoomLambda).isCalledOnce()
    }

    @Test
    fun `report room, leave=true, report=true, report room failed`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = failureReportRoomLambda
        )
        val client = FakePRISMClient().apply {
            givenGetRoomResult(roomId, room)
        }
        val reportRoom = DefaultReportRoom(client = client)

        val result = reportRoom(roomId, shouldReport = true, reason = "Spam", shouldLeave = true)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(ReportRoom.Exception.ReportRoomFailed)
        assert(successLeaveRoomLambda).isNeverCalled()
        assert(failureReportRoomLambda).isCalledOnce()
    }
}
