/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.features.startchat.impl.userlist.UserListEvents
import io.prism.android.features.startchat.impl.userlist.UserListState
import io.prism.android.features.startchat.impl.userlist.UserListStateProvider
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.ListSectionHeader
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.CheckableUserRow
import io.prism.android.libraries.prism.ui.components.CheckableUserRowData
import io.prism.android.libraries.prism.ui.components.SelectedUsersRowList
import io.prism.android.libraries.prism.ui.model.getAvatarData
import io.prism.android.libraries.prism.ui.model.getBestName
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun UserListView(
    state: UserListState,
    onSelectUser: (PRISMUser) -> Unit,
    onDeselectUser: (PRISMUser) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    Column(
        modifier = modifier,
    ) {
        SearchUserBar(
            modifier = Modifier.fillMaxWidth(),
            queryState = state.searchQuery,
            resultState = state.searchResults,
            selectedUsers = state.selectedUsers,
            active = state.isSearchActive,
            showLoader = state.showSearchLoader,
            isMultiSelectionEnable = state.isMultiSelectionEnabled,
            showBackButton = showBackButton,
            onActiveChange = { state.eventSink(UserListEvents.OnSearchActiveChanged(it)) },
            onUserSelect = {
                state.eventSink(UserListEvents.AddToSelection(it))
                onSelectUser(it)
            },
            onUserDeselect = {
                state.eventSink(UserListEvents.RemoveFromSelection(it))
                onDeselectUser(it)
            },
        )

        if (state.isMultiSelectionEnabled && !state.isSearchActive && state.selectedUsers.isNotEmpty()) {
            SelectedUsersRowList(
                contentPadding = PaddingValues(16.dp),
                selectedUsers = state.selectedUsers,
                autoScroll = true,
                onUserRemove = {
                    state.eventSink(UserListEvents.RemoveFromSelection(it))
                    onDeselectUser(it)
                },
            )
        }
        if (!state.isSearchActive && state.recentDirectRooms.isNotEmpty()) {
            LazyColumn {
                item {
                    ListSectionHeader(
                        title = stringResource(id = CommonStrings.common_suggestions),
                        hasDivider = false,
                    )
                }
                state.recentDirectRooms.forEachIndexed { index, recentDirectRoom ->
                    item {
                        val isSelected = state.selectedUsers.any {
                            recentDirectRoom.prismUser.userId == it.userId
                        }
                        CheckableUserRow(
                            checked = isSelected,
                            onCheckedChange = {
                                if (isSelected) {
                                    state.eventSink(UserListEvents.RemoveFromSelection(recentDirectRoom.prismUser))
                                    onDeselectUser(recentDirectRoom.prismUser)
                                } else {
                                    state.eventSink(UserListEvents.AddToSelection(recentDirectRoom.prismUser))
                                    onSelectUser(recentDirectRoom.prismUser)
                                }
                            },
                            data = CheckableUserRowData.Resolved(
                                avatarData = recentDirectRoom.prismUser.getAvatarData(AvatarSize.UserListItem),
                                name = recentDirectRoom.prismUser.getBestName(),
                                subtext = recentDirectRoom.prismUser.userId.value,
                            ),
                        )
                        if (index < state.recentDirectRooms.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun UserListViewPreview(@PreviewParameter(UserListStateProvider::class) state: UserListState) = PRISMPreview {
    UserListView(
        state = state,
        onSelectUser = {},
        onDeselectUser = {},
    )
}
