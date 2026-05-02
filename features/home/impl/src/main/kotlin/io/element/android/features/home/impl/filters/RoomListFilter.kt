/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.filters

import io.prism.android.features.home.impl.R
import io.prism.android.libraries.matrix.api.roomlist.RoomListFilter as PRISMRoomListFilter

/**
 * Enum class representing the different filters that can be applied to the room list.
 * Order is important, it'll be used as initial order in the UI.
 */
enum class RoomListFilter(val stringResource: Int) {
    Unread(R.string.screen_roomlist_filter_unreads),
    People(R.string.screen_roomlist_filter_people),
    Rooms(R.string.screen_roomlist_filter_rooms),
    Favourites(R.string.screen_roomlist_filter_favourites),
    Invites(R.string.screen_roomlist_filter_invites);

    val incompatibleFilters: Set<RoomListFilter>
        get() = when (this) {
            Rooms -> setOf(People, Invites)
            People -> setOf(Rooms, Invites)
            Unread -> setOf(Invites)
            Favourites -> setOf(Invites)
            Invites -> setOf(Rooms, People, Unread, Favourites)
        }
}

fun RoomListFilter.into(): PRISMRoomListFilter {
    return when (this) {
        RoomListFilter.Rooms -> PRISMRoomListFilter.Category.Group
        RoomListFilter.People -> PRISMRoomListFilter.Category.People
        RoomListFilter.Unread -> PRISMRoomListFilter.Unread
        RoomListFilter.Favourites -> PRISMRoomListFilter.Favorite
        RoomListFilter.Invites -> PRISMRoomListFilter.Invite
    }
}
