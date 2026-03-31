/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.roles

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_ID_2
import io.prism.android.libraries.prism.test.A_USER_ID_3
import io.prism.android.libraries.prism.test.A_USER_ID_4
import io.prism.android.libraries.prism.test.A_USER_ID_5
import io.prism.android.libraries.prism.test.A_USER_ID_6
import io.prism.android.libraries.prism.test.A_USER_ID_7
import io.prism.android.libraries.prism.test.room.aRoomMember
import io.prism.android.libraries.prism.ui.room.PowerLevelRoomMemberComparator
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class MembersByRoleTest {
    @Test
    fun `constructor - with single member list categorizes and sorts members`() {
        val members = listOf(
            aRoomMember(A_USER_ID_2, displayName = "Bob", role = RoomMember.Role.Admin),
            aRoomMember(A_USER_ID, displayName = "Alice", role = RoomMember.Role.Admin),
            aRoomMember(A_USER_ID_3, displayName = "Carol", role = RoomMember.Role.User),
            aRoomMember(A_USER_ID_5, displayName = "Eve", role = RoomMember.Role.User),
            aRoomMember(A_USER_ID_4, displayName = "David", role = RoomMember.Role.User),
            aRoomMember(A_USER_ID_6, displayName = "Justin", role = RoomMember.Role.Owner(isCreator = true)),
            aRoomMember(A_USER_ID_7, displayName = "Mallory", role = RoomMember.Role.Owner(isCreator = false)),
        )
        val membersByRole = MembersByRole(members = members, comparator = PowerLevelRoomMemberComparator())
        assertThat(membersByRole.owners).containsExactly(
            aRoomMember(A_USER_ID_6, displayName = "Justin", role = RoomMember.Role.Owner(isCreator = true)),
            aRoomMember(A_USER_ID_7, displayName = "Mallory", role = RoomMember.Role.Owner(isCreator = false)),
        )
        assertThat(membersByRole.admins).containsExactly(
            aRoomMember(A_USER_ID, displayName = "Alice", role = RoomMember.Role.Admin),
            aRoomMember(A_USER_ID_2, displayName = "Bob", role = RoomMember.Role.Admin),
        )
        assertThat(membersByRole.moderators).isEmpty()
        assertThat(membersByRole.members).containsExactly(
            aRoomMember(A_USER_ID_3, displayName = "Carol", role = RoomMember.Role.User),
            aRoomMember(A_USER_ID_4, displayName = "David", role = RoomMember.Role.User),
            aRoomMember(A_USER_ID_5, displayName = "Eve", role = RoomMember.Role.User),
        )
    }

    @Test
    fun `isEmpty - only returns true with no members of any role`() {
        val emptyMembersByRole = MembersByRole()
        assertThat(emptyMembersByRole.isEmpty()).isTrue()

        val membersByRoleWithOwners = MembersByRole(
            owners = persistentListOf(aRoomMember(A_USER_ID, role = RoomMember.Role.Admin)),
        )
        assertThat(membersByRoleWithOwners.isEmpty()).isFalse()

        val membersByRoleWithAdmins = MembersByRole(
            admins = persistentListOf(aRoomMember(A_USER_ID, role = RoomMember.Role.Admin)),
        )
        assertThat(membersByRoleWithAdmins.isEmpty()).isFalse()

        val membersByRoleWithModerators = MembersByRole(
            moderators = persistentListOf(aRoomMember(A_USER_ID, role = RoomMember.Role.Moderator)),
        )
        assertThat(membersByRoleWithModerators.isEmpty()).isFalse()

        val membersByRoleWithMembers = MembersByRole(
            members = persistentListOf(aRoomMember(A_USER_ID, role = RoomMember.Role.User)),
        )
        assertThat(membersByRoleWithMembers.isEmpty()).isFalse()
    }
}
