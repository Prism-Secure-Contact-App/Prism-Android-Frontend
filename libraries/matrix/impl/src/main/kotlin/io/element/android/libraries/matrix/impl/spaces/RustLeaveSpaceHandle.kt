/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.spaces

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.CurrentUserMembership
import io.prism.android.libraries.prism.api.room.RoomMembershipObserver
import io.prism.android.libraries.prism.api.spaces.LeaveSpaceHandle
import io.prism.android.libraries.prism.api.spaces.LeaveSpaceRoom
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import org.prism.rustcomponents.sdk.LeaveSpaceHandle as RustLeaveSpaceHandle

class RustLeaveSpaceHandle(
    override val id: RoomId,
    private val spaceRoomMapper: SpaceRoomMapper,
    private val roomMembershipObserver: RoomMembershipObserver,
    sessionCoroutineScope: CoroutineScope,
    private val innerProvider: suspend () -> RustLeaveSpaceHandle,
) : LeaveSpaceHandle {
    private val inner = CompletableDeferred<RustLeaveSpaceHandle>()

    init {
        sessionCoroutineScope.launch {
            inner.complete(innerProvider())
        }
    }

    override suspend fun rooms(): Result<List<LeaveSpaceRoom>> = runCatchingExceptions {
        inner.await().rooms().map { leaveSpaceRoom ->
            LeaveSpaceRoom(
                spaceRoom = spaceRoomMapper.map(leaveSpaceRoom.spaceRoom),
                isLastOwner = leaveSpaceRoom.isLastOwner,
                areCreatorsPrivileged = leaveSpaceRoom.areCreatorsPrivileged,
            )
        }
    }

    override suspend fun leave(roomIds: List<RoomId>): Result<Unit> = runCatchingExceptions {
        // Ensure the space is included and is the last room to be left
        val roomToLeave = roomIds - id + id
        inner.await().leave(roomToLeave.map { it.value })
    }.onSuccess {
        roomMembershipObserver.notifyUserLeftRoom(
            roomId = id,
            isSpace = true,
            membershipBeforeLeft = CurrentUserMembership.JOINED,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun close() {
        Timber.d("Destroying LeaveSpaceHandle $id")
        try {
            inner.getCompleted().destroy()
        } catch (_: Exception) {
            // Ignore, we just want to make sure it's completed
        }
    }
}
