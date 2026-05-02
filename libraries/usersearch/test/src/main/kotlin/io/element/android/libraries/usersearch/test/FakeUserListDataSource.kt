/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.usersearch.test

import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.usersearch.api.UserListDataSource

class FakeUserListDataSource : UserListDataSource {
    private var searchResult: List<PRISMUser> = emptyList()
    private var profile: PRISMUser? = null

    override suspend fun search(query: String, count: Long): List<PRISMUser> = searchResult.take(count.toInt())

    override suspend fun getProfile(userId: UserId): PRISMUser? = profile

    fun givenSearchResult(users: List<PRISMUser>) {
        this.searchResult = users
    }

    fun givenUserProfile(matrixUser: PRISMUser?) {
        this.profile = matrixUser
    }
}
