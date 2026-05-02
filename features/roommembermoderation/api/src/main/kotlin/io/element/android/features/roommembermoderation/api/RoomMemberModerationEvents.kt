/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.api

import io.prism.android.libraries.matrix.api.user.PRISMUser

interface RoomMemberModerationEvents {
    data class ShowActionsForUser(val user: PRISMUser) : RoomMemberModerationEvents
    data class ProcessAction(val action: ModerationAction, val targetUser: PRISMUser) : RoomMemberModerationEvents
}
