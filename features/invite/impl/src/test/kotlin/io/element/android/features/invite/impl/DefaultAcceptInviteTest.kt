/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl

import com.google.common.truth.Truth.assertThat
import uk.fathertkt.prism.features.analytics.plan.JoinedRoom
import io.prism.android.features.invite.test.InMemorySeenInvitesStore
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.RoomIdOrAlias
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.toRoomIdOrAlias
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.join.FakeJoinRoom
import io.prism.android.libraries.push.test.notifications.FakeNotificationCleaner
import io.prism.android.tests.testutils.lambda.any
import io.prism.android.tests.testutils.lambda.assert
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultAcceptInviteTest {
    private val roomId = A_ROOM_ID
    private val client = FakePRISMClient()
    private val seenInvitesStore = InMemorySeenInvitesStore(initialRoomIds = setOf(roomId))

    private val clearMembershipNotificationForRoomLambda =
        lambdaRecorder<SessionId, RoomId, Unit> { _, _ -> }
    private val notificationCleaner =
        FakeNotificationCleaner(clearMembershipNotificationForRoomLambda = clearMembershipNotificationForRoomLambda)

    @Test
    fun `accept invite success scenario`() = runTest {
        val joinRoomLambda =
            lambdaRecorder<RoomIdOrAlias, List<String>, JoinedRoom.Trigger, Result<Unit>> { _, _, _ ->
                Result.success(Unit)
            }

        val acceptInvite = DefaultAcceptInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            joinRoom = FakeJoinRoom(lambda = joinRoomLambda),
            seenInvitesStore = seenInvitesStore
        )

        val result = acceptInvite(roomId)

        assertThat(result.isSuccess).isTrue()

        assert(joinRoomLambda)
            .isCalledOnce()
            .with(value(roomId.toRoomIdOrAlias()), any(), any())

        assert(clearMembershipNotificationForRoomLambda)
            .isCalledOnce()
            .with(value(client.sessionId), value(roomId))

        assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
    }

    @Test
    fun `accept invite failure scenario`() = runTest {
        val joinRoomLambda =
            lambdaRecorder<RoomIdOrAlias, List<String>, JoinedRoom.Trigger, Result<Unit>> { _, _, _ ->
                Result.failure(RuntimeException("Join room failed"))
            }

        val acceptInvite = DefaultAcceptInvite(
            client = client,
            notificationCleaner = notificationCleaner,
            joinRoom = FakeJoinRoom(lambda = joinRoomLambda),
            seenInvitesStore = seenInvitesStore
        )

        val result = acceptInvite(roomId)

        assertThat(result.isFailure).isTrue()

        assert(joinRoomLambda)
            .isCalledOnce()
            .with(value(roomId.toRoomIdOrAlias()), any(), any())

        assert(clearMembershipNotificationForRoomLambda).isNeverCalled()

        assertThat(seenInvitesStore.seenRoomIds().first()).containsExactly(roomId)
    }
}
