/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl

import io.prism.android.features.home.impl.roomlist.RoomListState
import io.prism.android.features.home.impl.spacefilters.SpaceFiltersState
import io.prism.android.features.home.impl.spaces.HomeSpacesState
import io.prism.android.features.logout.api.direct.DirectLogoutState
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class HomeState(
    /**
     * The current user of this session, in case of multiple accounts, will contains 3 items, with the
     * current user in the middle.
     */
    val currentUserAndNeighbors: ImmutableList<PRISMUser>,
    val showAvatarIndicator: Boolean,
    val hasNetworkConnection: Boolean,
    val currentHomeNavigationBarItem: HomeNavigationBarItem,
    val roomListState: RoomListState,
    val homeSpacesState: HomeSpacesState,
    val snackbarMessage: SnackbarMessage?,
    val canReportBug: Boolean,
    val directLogoutState: DirectLogoutState,
    val eventSink: (HomeEvent) -> Unit,
) {
    val isBackHandlerEnabled = currentHomeNavigationBarItem != HomeNavigationBarItem.Chats || roomListState.spaceFiltersState is SpaceFiltersState.Selected
    val displayRoomListFilters = currentHomeNavigationBarItem == HomeNavigationBarItem.Chats && roomListState.displayFilters
    val showNavigationBar = homeSpacesState.canCreateSpaces || homeSpacesState.spaceRooms.isNotEmpty()
}
