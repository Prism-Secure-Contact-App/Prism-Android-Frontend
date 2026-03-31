/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.startchat.api.ConfirmingStartDmWithPRISMUser
import io.prism.android.features.startchat.impl.userlist.UserListState
import io.prism.android.features.startchat.impl.userlist.aRecentDirectRoomList
import io.prism.android.features.startchat.impl.userlist.aUserListState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.usersearch.api.UserSearchResult
import kotlinx.collections.immutable.persistentListOf

open class StartChatStateProvider : PreviewParameterProvider<StartChatState> {
    override val values: Sequence<StartChatState>
        get() = sequenceOf(
            aCreateRoomRootState(),
            aCreateRoomRootState(
                startDmAction = AsyncAction.Loading,
                userListState = aPRISMUser().let {
                    aUserListState(
                        searchQuery = it.userId.value,
                        searchResults = SearchBarResultState.Results(persistentListOf(UserSearchResult(it, false))),
                        selectedUsers = listOf(it),
                        isSearchActive = true,
                    )
                }
            ),
            aCreateRoomRootState(
                startDmAction = AsyncAction.Failure(RuntimeException("error")),
                userListState = aPRISMUser().let {
                    aUserListState(
                        searchQuery = it.userId.value,
                        searchResults = SearchBarResultState.Results(persistentListOf(UserSearchResult(it, false))),
                        selectedUsers = listOf(it),
                        isSearchActive = true,
                    )
                }
            ),
            aCreateRoomRootState(
                userListState = aUserListState(
                    recentDirectRooms = aRecentDirectRoomList()
                )
            ),
            aCreateRoomRootState(
                startDmAction = ConfirmingStartDmWithPRISMUser(aPRISMUser()),
            ),
            aCreateRoomRootState(
                isRoomDirectorySearchEnabled = true,
            ),
        )
}

fun aCreateRoomRootState(
    applicationName: String = "PRISM X Preview",
    userListState: UserListState = aUserListState(),
    startDmAction: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    isRoomDirectorySearchEnabled: Boolean = false,
    eventSink: (StartChatEvents) -> Unit = {},
) = StartChatState(
    applicationName = applicationName,
    userListState = userListState,
    startDmAction = startDmAction,
    isRoomDirectorySearchEnabled = isRoomDirectorySearchEnabled,
    eventSink = eventSink,
)
