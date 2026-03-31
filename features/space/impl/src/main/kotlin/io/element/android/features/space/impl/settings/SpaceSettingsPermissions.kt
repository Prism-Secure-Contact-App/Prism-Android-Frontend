/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.impl.settings

import io.prism.android.features.roomdetailsedit.api.RoomDetailsEditPermissions
import io.prism.android.features.roomdetailsedit.api.roomDetailsEditPermissions
import io.prism.android.features.securityandprivacy.api.SecurityAndPrivacyPermissions
import io.prism.android.features.securityandprivacy.api.securityAndPrivacyPermissions
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.room.powerlevels.RoomPermissions
import io.prism.android.libraries.prism.api.room.powerlevels.canEditRolesAndPermissions

data class SpaceSettingsPermissions(
    val editDetailsPermissions: RoomDetailsEditPermissions,
    val canEditRolesAndPermissions: Boolean,
    val securityAndPrivacyPermissions: SecurityAndPrivacyPermissions,
) {
    fun hasAny(joinRule: JoinRule?): Boolean {
        return editDetailsPermissions.hasAny ||
            canEditRolesAndPermissions ||
            securityAndPrivacyPermissions.hasAny(isSpace = true, joinRule = joinRule)
    }

    companion object {
        val DEFAULT = SpaceSettingsPermissions(
            editDetailsPermissions = RoomDetailsEditPermissions.DEFAULT,
            canEditRolesAndPermissions = false,
            securityAndPrivacyPermissions = SecurityAndPrivacyPermissions.DEFAULT,
        )
    }
}

fun RoomPermissions.spaceSettingsPermissions(): SpaceSettingsPermissions {
    return SpaceSettingsPermissions(
        editDetailsPermissions = roomDetailsEditPermissions(),
        canEditRolesAndPermissions = canEditRolesAndPermissions(),
        securityAndPrivacyPermissions = securityAndPrivacyPermissions(),
    )
}
