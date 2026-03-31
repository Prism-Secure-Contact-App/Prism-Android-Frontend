/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUserList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal class DefaultInvitePeopleStateProvider : PreviewParameterProvider<DefaultInvitePeopleState> {
    override val values: Sequence<DefaultInvitePeopleState>
        get() = sequenceOf(
            aDefaultInvitePeopleState(),
            aDefaultInvitePeopleState(canInvite = true, selectedUsers = aPRISMUserList().toImmutableList()),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query"),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query", selectedUsers = aPRISMUserList().toImmutableList()),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query", searchResults = SearchBarResultState.NoResultsFound()),
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "some query",
                selectedUsers = persistentListOf(
                    aPRISMUser("@carol:server.org", "Carol")
                ),
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aPRISMUser("@alice:server.org")),
                        anInvitableUser(aPRISMUser("@bob:server.org", "Bob")),
                        anInvitableUser(aPRISMUser("@carol:server.org", "Carol"), isSelected = true),
                        anInvitableUser(aPRISMUser("@eve:server.org", "Eve"), isSelected = true, isAlreadyJoined = true),
                        anInvitableUser(aPRISMUser("@justin:server.org", "Justin"), isSelected = true, isAlreadyInvited = true),
                    )
                )
            ),
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "@alice:server.org",
                selectedUsers = persistentListOf(
                    aPRISMUser("@carol:server.org", "Carol")
                ),
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aPRISMUser("@alice:server.org"), isUnresolved = true),
                        anInvitableUser(aPRISMUser("@bob:server.org", "Bob")),
                    )
                )
            ),
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "@alice:server.org",
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aPRISMUser("@alice:server.org"), isUnresolved = true),
                    )
                ),
                showSearchLoader = true,
            ),
            aDefaultInvitePeopleState(room = AsyncData.Failure(Exception("Room not found"))),
            aDefaultInvitePeopleState(
                canInvite = false,
                selectedUsers = aPRISMUserList().toImmutableList(),
                sendInvitesAction = AsyncAction.Loading,
            ),
        )
}

private fun anInvitableUser(
    prismUser: PRISMUser,
    isSelected: Boolean = false,
    isAlreadyJoined: Boolean = false,
    isAlreadyInvited: Boolean = false,
    isUnresolved: Boolean = false,
) = InvitableUser(
    prismUser = prismUser,
    isSelected = isSelected,
    isAlreadyJoined = isAlreadyJoined,
    isAlreadyInvited = isAlreadyInvited,
    isUnresolved = isUnresolved,
)

private fun aDefaultInvitePeopleState(
    room: AsyncData<Unit> = AsyncData.Success(Unit),
    canInvite: Boolean = false,
    searchQuery: String = "",
    searchResults: SearchBarResultState<ImmutableList<InvitableUser>> = SearchBarResultState.Initial(),
    selectedUsers: ImmutableList<PRISMUser> = persistentListOf(),
    isSearchActive: Boolean = false,
    showSearchLoader: Boolean = false,
    sendInvitesAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    suggestions: List<InvitableUser> = aPRISMUserList()
        .take(5)
        .map { user -> anInvitableUser(prismUser = user, isSelected = user in selectedUsers) },
): DefaultInvitePeopleState {
    return DefaultInvitePeopleState(
        room = room,
        canInvite = canInvite,
        searchQuery = TextFieldState(initialText = searchQuery),
        searchResults = searchResults,
        selectedUsers = selectedUsers,
        isSearchActive = isSearchActive,
        showSearchLoader = showSearchLoader,
        sendInvitesAction = sendInvitesAction,
        suggestions = suggestions.toImmutableList(),
        eventSink = {},
    )
}
