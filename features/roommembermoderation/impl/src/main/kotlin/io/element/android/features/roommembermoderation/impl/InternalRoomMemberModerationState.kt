/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.impl

import io.prism.android.features.roommembermoderation.api.ModerationActionState
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationPermissions
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class InternalRoomMemberModerationState(
    override val permissions: RoomMemberModerationPermissions,
    val selectedUser: PRISMUser?,
    val actions: ImmutableList<ModerationActionState>,
    val kickUserAsyncAction: AsyncAction<Unit>,
    val banUserAsyncAction: AsyncAction<Unit>,
    val unbanUserAsyncAction: AsyncAction<Unit>,
    override val eventSink: (RoomMemberModerationEvents) -> Unit,
) : RoomMemberModerationState {
    val canDisplayActions = actions.isNotEmpty()
}
