/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.userlist

import io.prism.android.libraries.matrix.api.user.PRISMUser

sealed interface UserListEvents {
    data class AddToSelection(val matrixUser: PRISMUser) : UserListEvents
    data class RemoveFromSelection(val matrixUser: PRISMUser) : UserListEvents
    data class OnSearchActiveChanged(val active: Boolean) : UserListEvents
}
