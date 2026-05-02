/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.matrix.api.room.BaseRoom
import io.prism.android.libraries.matrix.api.room.RoomMember
import io.prism.android.libraries.matrix.api.room.roomMembers
import kotlinx.coroutines.withContext

@Inject
class RoomMemberListDataSource(
    private val room: BaseRoom,
    private val coroutineDispatchers: CoroutineDispatchers,
) {
    suspend fun search(query: String): List<RoomMember> = withContext(coroutineDispatchers.io) {
        val roomMembersState = room.membersStateFlow.value
        val activeRoomMembers = roomMembersState.roomMembers()
            ?.filter { it.membership.isActive() }
            .orEmpty()
        val filteredMembers = if (query.isBlank()) {
            activeRoomMembers
        } else {
            activeRoomMembers.filter { member ->
                member.userId.value.contains(query, ignoreCase = true) ||
                        member.displayName?.contains(query, ignoreCase = true).orFalse()
            }
        }
        filteredMembers
    }
}
