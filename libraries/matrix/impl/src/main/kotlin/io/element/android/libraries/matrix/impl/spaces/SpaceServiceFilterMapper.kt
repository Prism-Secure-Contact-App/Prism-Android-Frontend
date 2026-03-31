/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.spaces

import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.spaces.SpaceServiceFilter
import org.prism.rustcomponents.sdk.SpaceFilter as RustSpaceFilter

class SpaceServiceFilterMapper(
    private val spaceRoomMapper: SpaceRoomMapper,
) {
    fun map(spaceFilter: RustSpaceFilter): SpaceServiceFilter {
        return SpaceServiceFilter(
            spaceRoom = spaceRoomMapper.map(spaceFilter.spaceRoom),
            level = spaceFilter.level.toInt(),
            descendants = spaceFilter.descendants.map { RoomId(it) },
        )
    }
}
