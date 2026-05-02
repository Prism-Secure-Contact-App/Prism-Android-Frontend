/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.room.preview

import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.preview.RoomPreviewInfo
import io.prism.android.libraries.matrix.impl.room.join.map
import io.prism.android.libraries.matrix.impl.room.map
import org.matrix.rustcomponents.sdk.RoomPreviewInfo as RustRoomPreviewInfo

object RoomPreviewInfoMapper {
    fun map(info: RustRoomPreviewInfo): RoomPreviewInfo {
        return RoomPreviewInfo(
            roomId = RoomId(info.roomId),
            canonicalAlias = info.canonicalAlias?.let(::RoomAlias),
            name = info.name,
            topic = info.topic,
            avatarUrl = info.avatarUrl,
            numberOfJoinedMembers = info.numJoinedMembers.toLong(),
            roomType = info.roomType.map(),
            isHistoryWorldReadable = info.isHistoryWorldReadable.orFalse(),
            membership = info.membership?.map(),
            joinRule = info.joinRule?.map(),
        )
    }
}
