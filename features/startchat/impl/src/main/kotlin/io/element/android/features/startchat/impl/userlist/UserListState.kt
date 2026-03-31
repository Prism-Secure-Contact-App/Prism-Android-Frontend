/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.userlist

import androidx.compose.foundation.text.input.TextFieldState
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.room.recent.RecentDirectRoom
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.usersearch.api.UserSearchResult
import kotlinx.collections.immutable.ImmutableList

data class UserListState(
    val searchQuery: TextFieldState,
    val searchResults: SearchBarResultState<ImmutableList<UserSearchResult>>,
    val showSearchLoader: Boolean,
    val selectedUsers: ImmutableList<PRISMUser>,
    val isSearchActive: Boolean,
    val selectionMode: SelectionMode,
    val recentDirectRooms: ImmutableList<RecentDirectRoom>,
    val eventSink: (UserListEvents) -> Unit,
) {
    val isMultiSelectionEnabled = selectionMode == SelectionMode.Multiple
}
