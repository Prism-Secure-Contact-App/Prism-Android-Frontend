/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.room.tombstone

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.tombstone.PredecessorRoom
import org.matrix.rustcomponents.sdk.PredecessorRoom as RustPredecessorRoom

fun RustPredecessorRoom.map(): PredecessorRoom {
    return PredecessorRoom(roomId = RoomId(roomId))
}
