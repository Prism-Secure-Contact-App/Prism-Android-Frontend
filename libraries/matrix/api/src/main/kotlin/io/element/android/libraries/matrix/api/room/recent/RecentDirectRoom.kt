/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.room.recent

import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.CurrentUserMembership
import io.prism.android.libraries.prism.api.room.isDm
import io.prism.android.libraries.prism.api.room.toPRISMUser
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class RecentDirectRoom(
    val roomId: RoomId,
    val prismUser: PRISMUser,
)

/**
 * Returns a [Flow] of [RecentDirectRoom] from recently visited DM rooms.
 * The flow emits items lazily, allowing callers to filter and take only what they need.
 * Use [kotlinx.coroutines.flow.take] to limit results and stop iteration early.
 */
fun PRISMClient.getRecentDirectRooms(): Flow<RecentDirectRoom> = flow {
    val foundUserIds = mutableSetOf<UserId>()
    val recentlyVisitedRooms = getRecentlyVisitedRooms().getOrDefault(emptyList())
    for (roomId in recentlyVisitedRooms) {
        getRoom(roomId)?.use { room ->
            val info = room.info()
            if (info.isDm && info.currentUserMembership == CurrentUserMembership.JOINED) {
                val otherUser = room.getDirectRoomMember()?.toPRISMUser()
                if (otherUser != null && foundUserIds.add(otherUser.userId)) {
                    emit(RecentDirectRoom(room.roomId, otherUser))
                }
            }
        }
    }
}
