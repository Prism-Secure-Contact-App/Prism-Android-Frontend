/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.userlist

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.recent.RecentDirectRoom
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUserList
import io.prism.android.libraries.usersearch.api.UserSearchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

open class UserListStateProvider : PreviewParameterProvider<UserListState> {
    override val values: Sequence<UserListState>
        get() = sequenceOf(
            aUserListState(),
            aUserListState(
                isSearchActive = false,
                selectedUsers = aListOfSelectedUsers(),
                selectionMode = SelectionMode.Multiple,
            ),
            aUserListState(isSearchActive = true),
            aUserListState(isSearchActive = true, searchQuery = "someone"),
            aUserListState(isSearchActive = true, searchQuery = "someone", selectionMode = SelectionMode.Multiple),
            aUserListState(
                isSearchActive = true,
                searchQuery = "@someone:prism.org",
                selectedUsers = aPRISMUserList().toImmutableList(),
                searchResults = SearchBarResultState.Results(aListOfUserSearchResults()),
            ),
            aUserListState(
                isSearchActive = true,
                searchQuery = "@someone:prism.org",
                selectionMode = SelectionMode.Multiple,
                selectedUsers = aPRISMUserList().toImmutableList(),
                searchResults = SearchBarResultState.Results(aListOfUserSearchResults()),
            ),
            aUserListState(
                isSearchActive = true,
                searchQuery = "something-with-no-results",
                searchResults = SearchBarResultState.NoResultsFound()
            ),
            aUserListState(
                isSearchActive = true,
                searchQuery = "someone",
                selectionMode = SelectionMode.Single,
            ),
            aUserListState(
                recentDirectRooms = aRecentDirectRoomList(),
            ),
        )
}

fun aUserListState(
    searchQuery: String = "",
    isSearchActive: Boolean = false,
    searchResults: SearchBarResultState<ImmutableList<UserSearchResult>> = SearchBarResultState.Initial(),
    selectedUsers: List<PRISMUser> = emptyList(),
    showSearchLoader: Boolean = false,
    selectionMode: SelectionMode = SelectionMode.Single,
    recentDirectRooms: List<RecentDirectRoom> = emptyList(),
    eventSink: (UserListEvents) -> Unit = {},
) = UserListState(
    searchQuery = TextFieldState(initialText = searchQuery),
    isSearchActive = isSearchActive,
    searchResults = searchResults,
    selectedUsers = selectedUsers.toImmutableList(),
    showSearchLoader = showSearchLoader,
    selectionMode = selectionMode,
    recentDirectRooms = recentDirectRooms.toImmutableList(),
    eventSink = eventSink
)

fun aListOfSelectedUsers() = aPRISMUserList().take(6).toImmutableList()
fun aListOfUserSearchResults() = aPRISMUserList().take(6).map { UserSearchResult(it) }.toImmutableList()

fun aRecentDirectRoomList(
    count: Int = 5
): List<RecentDirectRoom> = aPRISMUserList()
    .take(count)
    .map {
        RecentDirectRoom(RoomId("!aRoom:id"), it)
    }
