/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetailsedit.api

import io.prism.android.libraries.prism.api.room.StateEventType
import io.prism.android.libraries.prism.api.room.powerlevels.RoomPermissions

data class RoomDetailsEditPermissions(
    val canEditName: Boolean,
    val canEditTopic: Boolean,
    val canEditAvatar: Boolean,
) {
    val hasAny = canEditName ||
        canEditTopic ||
        canEditAvatar

    companion object {
        val DEFAULT = RoomDetailsEditPermissions(
            canEditName = false,
            canEditTopic = false,
            canEditAvatar = false,
        )
    }
}

fun RoomPermissions.roomDetailsEditPermissions(): RoomDetailsEditPermissions {
    return RoomDetailsEditPermissions(
        canEditName = canOwnUserSendState(StateEventType.RoomName),
        canEditTopic = canOwnUserSendState(StateEventType.RoomTopic),
        canEditAvatar = canOwnUserSendState(StateEventType.RoomAvatar),
    )
}
