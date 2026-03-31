/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.impl

import io.prism.android.features.invitepeople.api.InvitePeopleEvents
import io.prism.android.libraries.prism.api.user.PRISMUser

sealed interface DefaultInvitePeopleEvents : InvitePeopleEvents {
    data class ToggleUser(val user: PRISMUser) : DefaultInvitePeopleEvents
    data class OnSearchActiveChanged(val active: Boolean) : DefaultInvitePeopleEvents
}
