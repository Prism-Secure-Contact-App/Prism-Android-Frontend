/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.history

import io.prism.android.libraries.prism.api.room.history.RoomHistoryVisibility
import org.prism.rustcomponents.sdk.RoomHistoryVisibility as RustRoomHistoryVisibility

fun RoomHistoryVisibility.map(): RustRoomHistoryVisibility {
    return when (this) {
        RoomHistoryVisibility.WorldReadable -> RustRoomHistoryVisibility.WorldReadable
        RoomHistoryVisibility.Invited -> RustRoomHistoryVisibility.Invited
        RoomHistoryVisibility.Joined -> RustRoomHistoryVisibility.Joined
        RoomHistoryVisibility.Shared -> RustRoomHistoryVisibility.Shared
        is RoomHistoryVisibility.Custom -> RustRoomHistoryVisibility.Custom(value)
    }
}

fun RustRoomHistoryVisibility.map(): RoomHistoryVisibility {
    return when (this) {
        RustRoomHistoryVisibility.WorldReadable -> RoomHistoryVisibility.WorldReadable
        RustRoomHistoryVisibility.Invited -> RoomHistoryVisibility.Invited
        RustRoomHistoryVisibility.Joined -> RoomHistoryVisibility.Joined
        RustRoomHistoryVisibility.Shared -> RoomHistoryVisibility.Shared
        is RustRoomHistoryVisibility.Custom -> RoomHistoryVisibility.Custom(value)
    }
}
