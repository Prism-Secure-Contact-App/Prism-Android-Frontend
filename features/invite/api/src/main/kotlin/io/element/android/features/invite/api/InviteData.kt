/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.api

import android.os.Parcelable
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.RoomInfo
import io.prism.android.libraries.prism.api.room.isDm
import io.prism.android.libraries.prism.api.room.preview.RoomPreviewInfo
import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import kotlinx.parcelize.Parcelize

@Parcelize
data class InviteData(
    val roomId: RoomId,
    val roomName: String,
    val isDm: Boolean,
) : Parcelable

fun RoomPreviewInfo.toInviteData(): InviteData {
    return InviteData(
        roomId = roomId,
        roomName = name ?: roomId.value,
        isDm = false,
    )
}

fun RoomInfo.toInviteData(): InviteData {
    return InviteData(
        roomId = id,
        roomName = name ?: id.value,
        isDm = isDm,
    )
}

fun SpaceRoom.toInviteData(): InviteData {
    return InviteData(
        roomId = roomId,
        roomName = displayName,
        isDm = false,
    )
}
