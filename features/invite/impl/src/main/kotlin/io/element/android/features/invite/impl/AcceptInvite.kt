/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl

import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.JoinedRoom
import io.prism.android.features.invite.api.SeenInvitesStore
import io.prism.android.libraries.core.extensions.mapFailure
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.prism.android.libraries.matrix.api.exception.ClientException
import io.prism.android.libraries.matrix.api.exception.ErrorKind
import io.prism.android.libraries.matrix.api.room.join.JoinRoom
import io.prism.android.libraries.push.api.notifications.NotificationCleaner

interface AcceptInvite {
    suspend operator fun invoke(roomId: RoomId): Result<RoomId>

    sealed class Failures : Exception() {
        data object InvalidInvite : Failures()
    }
}

@ContributesBinding(SessionScope::class)
class DefaultAcceptInvite(
    private val client: PRISMClient,
    private val joinRoom: JoinRoom,
    private val notificationCleaner: NotificationCleaner,
    private val seenInvitesStore: SeenInvitesStore,
) : AcceptInvite {
    override suspend fun invoke(roomId: RoomId): Result<RoomId> {
        return joinRoom(
            roomIdOrAlias = roomId.toRoomIdOrAlias(),
            serverNames = emptyList(),
            trigger = JoinedRoom.Trigger.Invite,
        ).onSuccess {
            notificationCleaner.clearMembershipNotificationForRoom(client.sessionId, roomId)
            seenInvitesStore.markAsUnSeen(roomId)
        }.mapFailure {
            if (it is ClientException.PRISMApi) {
                when (it.kind) {
                    ErrorKind.Unknown -> AcceptInvite.Failures.InvalidInvite
                    else -> it
                }
            } else {
                it
            }
        }.map { roomId }
    }
}
