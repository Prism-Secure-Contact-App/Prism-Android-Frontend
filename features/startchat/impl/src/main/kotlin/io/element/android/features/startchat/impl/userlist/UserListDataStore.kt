/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.userlist

import dev.zacsweers.metro.Inject
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
class UserListDataStore {
    private val _selectedUsers: MutableStateFlow<List<PRISMUser>> = MutableStateFlow(emptyList())

    fun selectUser(user: PRISMUser) {
        if (!_selectedUsers.value.contains(user)) {
            _selectedUsers.tryEmit(_selectedUsers.value.plus(user))
        }
    }

    fun removeUserFromSelection(user: PRISMUser) {
        _selectedUsers.tryEmit(_selectedUsers.value.minus(user))
    }

    val selectedUsers = _selectedUsers.asStateFlow()
}
