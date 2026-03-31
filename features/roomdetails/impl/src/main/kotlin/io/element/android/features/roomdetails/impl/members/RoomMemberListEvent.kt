/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl.members

import io.prism.android.libraries.prism.api.room.RoomMember

sealed interface RoomMemberListEvent {
    data class ChangeSelectedSection(val section: SelectedSection) : RoomMemberListEvent
    data class RoomMemberSelected(val roomMember: RoomMember) : RoomMemberListEvent
}
