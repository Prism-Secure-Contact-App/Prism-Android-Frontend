/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.leaveroom.impl

import androidx.compose.runtime.Immutable
import io.prism.android.features.leaveroom.api.LeaveRoomEvent
import io.prism.android.features.leaveroom.api.LeaveRoomState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.RoomId

data class InternalLeaveRoomState(
    val leaveAction: AsyncAction<Unit>,
    override val eventSink: (LeaveRoomEvent) -> Unit
) : LeaveRoomState

@Immutable
sealed interface Confirmation : AsyncAction.Confirming {
    data class Dm(val roomId: RoomId) : Confirmation
    data class Generic(val roomId: RoomId) : Confirmation
    data class PrivateRoom(val roomId: RoomId) : Confirmation
    data class LastUserInRoom(val roomId: RoomId) : Confirmation
    data class LastOwnerInRoom(val roomId: RoomId) : Confirmation
}
