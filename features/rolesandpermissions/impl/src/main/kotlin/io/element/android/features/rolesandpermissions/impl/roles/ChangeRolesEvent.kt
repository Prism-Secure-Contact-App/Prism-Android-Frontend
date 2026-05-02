/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.roles

import io.prism.android.libraries.matrix.api.user.PRISMUser

sealed interface ChangeRolesEvent {
    data object ToggleSearchActive : ChangeRolesEvent
    data class UserSelectionToggled(val matrixUser: PRISMUser) : ChangeRolesEvent
    data object Save : ChangeRolesEvent
    data object Exit : ChangeRolesEvent
    data object CloseDialog : ChangeRolesEvent
}
