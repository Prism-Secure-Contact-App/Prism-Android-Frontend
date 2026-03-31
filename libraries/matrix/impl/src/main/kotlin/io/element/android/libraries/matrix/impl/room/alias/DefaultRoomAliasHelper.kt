/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.alias

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.room.alias.RoomAliasHelper

@ContributesBinding(AppScope::class)
class DefaultRoomAliasHelper : RoomAliasHelper {
    override fun roomAliasNameFromRoomDisplayName(name: String): String {
        return org.prism.rustcomponents.sdk.roomAliasNameFromRoomDisplayName(name)
    }

    override fun isRoomAliasValid(roomAlias: RoomAlias): Boolean {
        return org.prism.rustcomponents.sdk.isRoomAliasFormatValid(roomAlias.value)
    }
}
