/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.usersearch

import io.prism.android.libraries.prism.api.user.PRISMSearchUserResults
import io.prism.android.libraries.prism.impl.mapper.map
import kotlinx.collections.immutable.toImmutableList
import org.prism.rustcomponents.sdk.SearchUsersResults

object UserSearchResultMapper {
    fun map(result: SearchUsersResults): PRISMSearchUserResults {
        return PRISMSearchUserResults(
            results = result.results
                .map { userProfile -> userProfile.map() }
                .toImmutableList(),
            limited = result.limited,
        )
    }
}
