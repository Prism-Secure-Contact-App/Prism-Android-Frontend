/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.room.powerlevels

import io.prism.android.libraries.prism.api.room.BaseRoom
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.api.room.RoomMembersState
import io.prism.android.libraries.prism.api.room.activeRoomMembers
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Return a flow of the list of active room members who match the predicate.
 */
fun BaseRoom.usersWithRole(predicate: (RoomMember.Role) -> Boolean): Flow<ImmutableList<RoomMember>> {
    // Wait until members are ready to avoid returning empty lists initially
    val readyMembersFlow = membersStateFlow
        .onStart {
            if (membersStateFlow.value is RoomMembersState.Unknown) {
                updateMembers()
            }
        }
        .filter { it is RoomMembersState.Ready }

    return readyMembersFlow.map { membersState ->
        membersState
            .activeRoomMembers()
            .filter { predicate(it.role) }
            .toImmutableList()
    }.distinctUntilChanged()
}

/**
 * Return the number of active room members who match the predicate.
 */
fun BaseRoom.userCountWithRole(predicate: (RoomMember.Role) -> Boolean): Flow<Int> {
    return usersWithRole(predicate).map { it.size }
}
