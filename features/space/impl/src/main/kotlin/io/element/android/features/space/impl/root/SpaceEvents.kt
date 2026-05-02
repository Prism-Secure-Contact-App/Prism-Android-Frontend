/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.impl.root

import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.spaces.SpaceRoom

sealed interface SpaceEvents {
    data object LoadMore : SpaceEvents
    data class Join(val spaceRoom: SpaceRoom) : SpaceEvents
    data object ClearFailures : SpaceEvents
    data class AcceptInvite(val spaceRoom: SpaceRoom) : SpaceEvents
    data class DeclineInvite(val spaceRoom: SpaceRoom) : SpaceEvents

    data class ShowTopicViewer(val topic: String) : SpaceEvents
    data object HideTopicViewer : SpaceEvents

    // Manage mode events
    data object EnterManageMode : SpaceEvents
    data object ExitManageMode : SpaceEvents
    data class ToggleRoomSelection(val roomId: RoomId) : SpaceEvents
    data object ConfirmRoomRemoval : SpaceEvents
    data object RemoveSelectedRooms : SpaceEvents
    data object ClearRemoveAction : SpaceEvents
}
