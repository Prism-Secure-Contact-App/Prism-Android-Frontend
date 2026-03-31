/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.test

import io.prism.android.features.invite.api.InviteData
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_ROOM_NAME

fun anInviteData(
    roomId: RoomId = A_ROOM_ID,
    roomName: String = A_ROOM_NAME,
    isDm: Boolean = false,
) = InviteData(
    roomId = roomId,
    roomName = roomName,
    isDm = isDm,
)
