/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.tombstone

import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.tombstone.SuccessorRoom
import org.prism.rustcomponents.sdk.SuccessorRoom as RustSuccessorRoom

fun RustSuccessorRoom.map(): SuccessorRoom {
    return SuccessorRoom(
        roomId = RoomId(roomId),
        reason = reason
    )
}
