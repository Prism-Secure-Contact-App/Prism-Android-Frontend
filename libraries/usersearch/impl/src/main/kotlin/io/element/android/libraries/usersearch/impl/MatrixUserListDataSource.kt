/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.usersearch.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.usersearch.api.UserListDataSource

@ContributesBinding(SessionScope::class)
class MatrixUserListDataSource(
    private val client: PRISMClient
) : UserListDataSource {
    override suspend fun search(query: String, count: Long): List<PRISMUser> {
        val res = client.searchUsers(query, count)
        return res.getOrNull()?.results.orEmpty()
    }

    override suspend fun getProfile(userId: UserId): PRISMUser? {
        return client.getProfile(userId).getOrNull()
    }
}
