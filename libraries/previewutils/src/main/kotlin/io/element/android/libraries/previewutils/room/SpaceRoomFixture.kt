/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.previewutils.room

import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.CurrentUserMembership
import io.prism.android.libraries.matrix.api.room.RoomType
import io.prism.android.libraries.matrix.api.room.join.JoinRule
import io.prism.android.libraries.matrix.api.spaces.SpaceRoom
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.toImmutableList

fun aSpaceRoom(
    rawName: String? = null,
    displayName: String = "Space name",
    avatarUrl: String? = null,
    canonicalAlias: RoomAlias? = null,
    childrenCount: Int = 0,
    guestCanJoin: Boolean = false,
    heroes: List<PRISMUser> = emptyList(),
    joinRule: JoinRule? = null,
    numJoinedMembers: Int = 0,
    roomId: RoomId = RoomId("!roomId:example.com"),
    roomType: RoomType = RoomType.Space,
    state: CurrentUserMembership? = null,
    topic: String? = null,
    worldReadable: Boolean = false,
    isDirect: Boolean? = null,
    via: List<String> = emptyList(),
) = SpaceRoom(
    rawName = rawName,
    displayName = displayName,
    avatarUrl = avatarUrl,
    canonicalAlias = canonicalAlias,
    childrenCount = childrenCount,
    guestCanJoin = guestCanJoin,
    heroes = heroes.toImmutableList(),
    joinRule = joinRule,
    numJoinedMembers = numJoinedMembers,
    roomId = roomId,
    roomType = roomType,
    state = state,
    topic = topic,
    worldReadable = worldReadable,
    via = via.toImmutableList(),
    isDirect = isDirect
)
