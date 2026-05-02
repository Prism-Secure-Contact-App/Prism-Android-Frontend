/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.usersearch.api

import io.prism.android.libraries.matrix.api.user.PRISMUser

data class UserSearchResult(
    val matrixUser: PRISMUser,
    val isUnresolved: Boolean = false,
)

data class UserSearchResultState(
    val results: List<UserSearchResult>,
    val isSearching: Boolean,
)
