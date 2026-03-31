/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.invite.test.InMemorySeenInvitesStore
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.aRoomInfo
import io.prism.android.libraries.prism.test.room.aRoomMember
import io.prism.android.libraries.push.test.notifications.FakeNotificationCleaner
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultDeclineInviteTest {
    private val roomId = A_ROOM_ID
    private val inviter = aRoomMember()
    private val seenInvitesStore = InMemorySeenInvitesStore(initialRoomIds = setOf(roomId))
    private val clearMembershipNotificationForRoomLambda =
        lambdaRecorder<SessionId, RoomId, Unit> { _, _ -> }
    private val notificationCleaner =
        FakeNotificationCleaner(clearMembershipNotificationForRoomLambda = clearMembershipNotificationForRoomLambda)

    private val successLeaveRoomLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
    private val successIgnoreUserLambda =
        lambdaRecorder<UserId, Result<Unit>> { _ -> Result.success(Unit) }
    private val successReportRoomLambda =
        lambdaRecorder<String?, Result<Unit>> { _ -> Result.success(Unit) }

    private val failureLeaveRoomLambda =
        lambdaRecorder<Result<Unit>> { Result.failure(Exception("Leave room error")) }
    private val failureIgnoreUserLambda =
        lambdaRecorder<UserId, Result<Unit>> { _ -> Result.failure(Exception("Ignore user error")) }
    private val failureReportRoomLambda =
        lambdaRecorder<String?, Result<Unit>> { _ -> Result.failure(Exception("Report room error")) }

    @Test
    fun `decline invite, block=false, report=false, all success`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient(ignoreUserResult = successIgnoreUserLambda).apply {
            givenGetRoomResult(roomId, room)
        }

        val declineInvite = DefaultDeclineInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            seenInvitesStore = seenInvitesStore
        )

        val result =
            declineInvite(roomId, blockUser = false, reportRoom = false, reportReason = null)

        assertThat(result.isSuccess).isTrue()

        assert(clearMembershipNotificationForRoomLambda)
            .isCalledOnce()
            .with(value(client.sessionId), value(roomId))

        assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
    }

    @Test
    fun `decline invite, block=true, report=true, all success`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda,
            initialRoomInfo = aRoomInfo(inviter = inviter)
        )
        val client = FakePRISMClient(ignoreUserResult = successIgnoreUserLambda).apply {
            givenGetRoomResult(roomId, room)
        }
        val declineInvite = DefaultDeclineInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            seenInvitesStore = seenInvitesStore
        )
        val result = declineInvite(roomId, blockUser = true, reportRoom = true, reportReason = null)

        assertThat(result.isSuccess).isTrue()

        assert(clearMembershipNotificationForRoomLambda)
            .isCalledOnce()
            .with(value(client.sessionId), value(roomId))

        assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
    }

    @Test
    fun `decline invite, block=true, report=true, decline invite failed`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = failureLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda
        )
        val client = FakePRISMClient(ignoreUserResult = successIgnoreUserLambda).apply {
            givenGetRoomResult(roomId, room)
        }
        val declineInvite = DefaultDeclineInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            seenInvitesStore = seenInvitesStore
        )
        val result = declineInvite(roomId, blockUser = true, reportRoom = true, reportReason = null)

        assertThat(result.exceptionOrNull()).isEqualTo(DeclineInvite.Exception.DeclineInviteFailed)

        assert(clearMembershipNotificationForRoomLambda)
            .isNeverCalled()

        assertThat(seenInvitesStore.seenRoomIds().first()).isNotEmpty()
    }

    @Test
    fun `decline invite, block=true, report=true, ignore user failed`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = successReportRoomLambda,
            initialRoomInfo = aRoomInfo(inviter = inviter)
        )
        val client = FakePRISMClient(ignoreUserResult = failureIgnoreUserLambda).apply {
            givenGetRoomResult(roomId, room)
        }
        val declineInvite = DefaultDeclineInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            seenInvitesStore = seenInvitesStore
        )
        val result = declineInvite(roomId, blockUser = true, reportRoom = true, reportReason = null)

        assertThat(result.exceptionOrNull()).isEqualTo(DeclineInvite.Exception.BlockUserFailed)

        assert(clearMembershipNotificationForRoomLambda).isCalledOnce()
        assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
    }

    @Test
    fun `decline invite, block=true, report=true, report room failed`() = runTest {
        val room = FakeBaseRoom(
            roomId = roomId,
            leaveRoomLambda = successLeaveRoomLambda,
            reportRoomResult = failureReportRoomLambda,
            initialRoomInfo = aRoomInfo(inviter = inviter)
        )
        val client = FakePRISMClient(ignoreUserResult = successIgnoreUserLambda).apply {
            givenGetRoomResult(roomId, room)
        }
        val declineInvite = DefaultDeclineInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            seenInvitesStore = seenInvitesStore
        )
        val result = declineInvite(roomId, blockUser = true, reportRoom = true, reportReason = null)

        assertThat(result.exceptionOrNull()).isEqualTo(DeclineInvite.Exception.ReportRoomFailed)

        assert(clearMembershipNotificationForRoomLambda).isCalledOnce()
        assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
    }
}
