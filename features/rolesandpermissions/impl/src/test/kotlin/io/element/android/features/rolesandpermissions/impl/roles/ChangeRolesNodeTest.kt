/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.roles

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import io.prism.android.libraries.prism.api.room.RoomMember
import org.junit.Test

class ChangeRolesNodeTest {
    @Test
    fun `test toRoomMemberRole`() {
        assertThat(ChangeRoomMemberRolesListType.Admins.toRoomMemberRole())
            .isEqualTo(RoomMember.Role.Admin)
        assertThat(ChangeRoomMemberRolesListType.Moderators.toRoomMemberRole())
            .isEqualTo(RoomMember.Role.Moderator)
        assertThat(ChangeRoomMemberRolesListType.SelectNewOwnersWhenLeaving.toRoomMemberRole())
            .isEqualTo(RoomMember.Role.Owner(false))
    }
}
