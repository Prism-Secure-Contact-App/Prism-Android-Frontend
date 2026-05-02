/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.root

import io.prism.android.features.rolesandpermissions.impl.R
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.room.RoomMember
import kotlinx.collections.immutable.ImmutableList

data class RolesAndPermissionsState(
    val roomSupportsOwnerRole: Boolean,
    val adminCount: Int?,
    val moderatorCount: Int?,
    val availableSelfDemoteActions: ImmutableList<SelfDemoteAction>,
    val changeOwnRoleAction: AsyncAction<Unit>,
    val resetPermissionsAction: AsyncAction<Unit>,
    val eventSink: (RolesAndPermissionsEvents) -> Unit,
) {
    val canSelfDemote = availableSelfDemoteActions.isNotEmpty()
}

enum class SelfDemoteAction(val role: RoomMember.Role, val titleRes: Int) {
    ToModerator(RoomMember.Role.Moderator, R.string.screen_room_roles_and_permissions_change_role_demote_to_moderator),
    ToMember(RoomMember.Role.User, R.string.screen_room_roles_and_permissions_change_role_demote_to_member)
}
