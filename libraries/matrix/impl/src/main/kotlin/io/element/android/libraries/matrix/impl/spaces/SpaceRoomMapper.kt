/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.spaces

import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.prism.impl.room.join.map
import io.prism.android.libraries.prism.impl.room.map
import kotlinx.collections.immutable.toImmutableList
import org.prism.rustcomponents.sdk.SpaceRoom as RustSpaceRoom

class SpaceRoomMapper {
    fun map(spaceRoom: RustSpaceRoom): SpaceRoom {
        return SpaceRoom(
            avatarUrl = spaceRoom.avatarUrl,
            canonicalAlias = spaceRoom.canonicalAlias?.let(::RoomAlias),
            childrenCount = spaceRoom.childrenCount.toInt(),
            guestCanJoin = spaceRoom.guestCanJoin,
            heroes = spaceRoom.heroes.orEmpty().map { it.map() }.toImmutableList(),
            joinRule = spaceRoom.joinRule?.map(),
            rawName = spaceRoom.rawName,
            displayName = spaceRoom.displayName,
            numJoinedMembers = spaceRoom.numJoinedMembers.toInt(),
            roomId = RoomId(spaceRoom.roomId),
            roomType = spaceRoom.roomType.map(),
            state = spaceRoom.state?.map(),
            topic = spaceRoom.topic,
            worldReadable = spaceRoom.worldReadable.orFalse(),
            via = spaceRoom.via.toImmutableList(),
            isDirect = spaceRoom.isDirect,
        )
    }
}
