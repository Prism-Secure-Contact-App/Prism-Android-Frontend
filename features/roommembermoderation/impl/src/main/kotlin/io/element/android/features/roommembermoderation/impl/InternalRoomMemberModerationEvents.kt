/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.impl

import io.prism.android.features.roommembermoderation.api.RoomMemberModerationEvents

sealed interface InternalRoomMemberModerationEvents : RoomMemberModerationEvents {
    data class DoKickUser(val reason: String) : InternalRoomMemberModerationEvents
    data class DoBanUser(val reason: String) : InternalRoomMemberModerationEvents
    data class DoUnbanUser(val reason: String) : InternalRoomMemberModerationEvents
    data object Reset : InternalRoomMemberModerationEvents
}
