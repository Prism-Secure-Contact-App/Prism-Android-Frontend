/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdirectory.impl.root.model

import io.prism.android.features.roomdirectory.api.RoomDescription
import io.prism.android.libraries.prism.api.roomdirectory.RoomDescription as PRISMRoomDescription

fun PRISMRoomDescription.toFeatureModel(): RoomDescription {
    return RoomDescription(
        roomId = roomId,
        name = name,
        alias = alias,
        topic = topic,
        avatarUrl = avatarUrl,
        numberOfMembers = numberOfMembers,
        joinRule = when (joinRule) {
            PRISMRoomDescription.JoinRule.PUBLIC -> RoomDescription.JoinRule.PUBLIC
            PRISMRoomDescription.JoinRule.KNOCK -> RoomDescription.JoinRule.KNOCK
            PRISMRoomDescription.JoinRule.RESTRICTED -> RoomDescription.JoinRule.RESTRICTED
            PRISMRoomDescription.JoinRule.KNOCK_RESTRICTED -> RoomDescription.JoinRule.KNOCK_RESTRICTED
            PRISMRoomDescription.JoinRule.INVITE -> RoomDescription.JoinRule.INVITE
            PRISMRoomDescription.JoinRule.UNKNOWN -> RoomDescription.JoinRule.UNKNOWN
        }
    )
}
