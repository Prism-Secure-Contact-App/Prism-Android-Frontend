/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.member

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.api.room.RoomMembershipState
import io.prism.android.libraries.prism.impl.room.powerlevels.into
import uniffi.prism_sdk.RoomMemberRole
import org.prism.rustcomponents.sdk.MembershipState as RustMembershipState
import org.prism.rustcomponents.sdk.RoomMember as RustRoomMember

object RoomMemberMapper {
    fun map(roomMember: RustRoomMember): RoomMember {
        val powerLevel = roomMember.powerLevel.into()
        return RoomMember(
            userId = UserId(roomMember.userId),
            displayName = roomMember.displayName,
            avatarUrl = roomMember.avatarUrl,
            membership = mapMembership(roomMember.membership),
            isNameAmbiguous = roomMember.isNameAmbiguous,
            powerLevel = powerLevel,
            isIgnored = roomMember.isIgnored,
            role = mapRole(roomMember.suggestedRoleForPowerLevel, powerLevel),
            membershipChangeReason = roomMember.membershipChangeReason
        )
    }

    fun mapRole(role: RoomMemberRole, powerLevel: Long?): RoomMember.Role =
        when (role) {
            RoomMemberRole.CREATOR -> RoomMember.Role.Owner(isCreator = true)
            RoomMemberRole.ADMINISTRATOR -> {
                val superAdmin = RoomMember.Role.Owner(isCreator = false)
                val powerLevelOrDefault = powerLevel ?: 0L
                if (powerLevelOrDefault >= superAdmin.powerLevel) {
                    superAdmin
                } else {
                    RoomMember.Role.Admin
                }
            }
            RoomMemberRole.MODERATOR -> RoomMember.Role.Moderator
            RoomMemberRole.USER -> RoomMember.Role.User
        }

    fun mapMembership(membershipState: RustMembershipState): RoomMembershipState =
        when (membershipState) {
            RustMembershipState.Ban -> RoomMembershipState.BAN
            RustMembershipState.Invite -> RoomMembershipState.INVITE
            RustMembershipState.Join -> RoomMembershipState.JOIN
            RustMembershipState.Knock -> RoomMembershipState.KNOCK
            RustMembershipState.Leave -> RoomMembershipState.LEAVE
            is RustMembershipState.Custom -> TODO()
        }
}
