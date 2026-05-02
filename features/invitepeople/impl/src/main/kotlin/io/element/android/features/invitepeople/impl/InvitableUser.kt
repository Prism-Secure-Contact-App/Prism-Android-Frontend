/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.impl

import io.prism.android.libraries.matrix.api.user.PRISMUser

data class InvitableUser(
    val matrixUser: PRISMUser,
    val isSelected: Boolean,
    val isAlreadyJoined: Boolean,
    val isAlreadyInvited: Boolean,
    val isUnresolved: Boolean,
)
