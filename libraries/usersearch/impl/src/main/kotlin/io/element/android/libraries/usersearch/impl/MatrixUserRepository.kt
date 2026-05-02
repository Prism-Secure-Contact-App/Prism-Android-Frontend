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
import io.prism.android.libraries.matrix.api.core.PRISMPatterns
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.usersearch.api.UserListDataSource
import io.prism.android.libraries.usersearch.api.UserRepository
import io.prism.android.libraries.usersearch.api.UserSearchResult
import io.prism.android.libraries.usersearch.api.UserSearchResultState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ContributesBinding(SessionScope::class)
class MatrixUserRepository(
    private val client: PRISMClient,
    private val dataSource: UserListDataSource
) : UserRepository {
    override fun search(query: String): Flow<UserSearchResultState> = flow {
        val shouldQueryProfile = PRISMPatterns.isUserId(query) && !client.isMe(UserId(query))
        val shouldFetchSearchResults = query.length >= MINIMUM_SEARCH_LENGTH
        // If the search term is a MXID that's not ours, we'll show a 'fake' result for that user, then update it when we get search results.
        val fakeSearchResult = if (shouldQueryProfile) {
            UserSearchResult(PRISMUser(UserId(query)))
        } else {
            null
        }
        if (shouldQueryProfile || shouldFetchSearchResults) {
            emit(UserSearchResultState(isSearching = shouldFetchSearchResults, results = listOfNotNull(fakeSearchResult)))
        }
        if (shouldFetchSearchResults) {
            val results = fetchSearchResults(query, shouldQueryProfile)
            emit(results)
        }
    }

    private suspend fun fetchSearchResults(query: String, shouldQueryProfile: Boolean): UserSearchResultState {
        // Debounce
        delay(DEBOUNCE_TIME_MILLIS)
        val results = dataSource
            .search(query, MAXIMUM_SEARCH_RESULTS)
            .filter { !client.isMe(it.userId) }
            .map { UserSearchResult(it) }
            .toMutableList()

        // If the query is another user's MXID and the result doesn't contain that user ID, query the profile information explicitly
        if (shouldQueryProfile && results.none { it.matrixUser.userId.value == query }) {
            results.add(
                0,
                dataSource.getProfile(UserId(query))
                    ?.let { UserSearchResult(it) }
                    ?: UserSearchResult(PRISMUser(UserId(query)), isUnresolved = true)
            )
        }

        return UserSearchResultState(results = results, isSearching = false)
    }

    companion object {
        private const val DEBOUNCE_TIME_MILLIS = 250L
        private const val MINIMUM_SEARCH_LENGTH = 3
        private const val MAXIMUM_SEARCH_RESULTS = 10L
    }
}
