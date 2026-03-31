/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.spacefilters

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.spaces.SpaceServiceFilter
import io.prism.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.toImmutableList

class SpaceFiltersStateProvider : PreviewParameterProvider<SpaceFiltersState> {
    override val values: Sequence<SpaceFiltersState>
        get() = sequenceOf(
            aSelectingSpaceFiltersState(),
            aSelectingSpaceFiltersState(searchQuery = "Pr")
        )
}

fun aDisabledSpaceFiltersState() = SpaceFiltersState.Disabled

fun anUnselectedSpaceFiltersState(
    eventSink: (SpaceFiltersEvent.Unselected) -> Unit = {},
) = SpaceFiltersState.Unselected(
    eventSink = eventSink,
)

fun aSelectingSpaceFiltersState(
    availableFilters: List<SpaceServiceFilter> = listOf(
        aSpaceServiceFilter(
            displayName = "Work",
            canonicalAlias = RoomAlias("#work:example.com"),
        ),
        aSpaceServiceFilter(
            displayName = "Personal",
            roomId = RoomId("!personal:example.com"),
        ),
        aSpaceServiceFilter(
            displayName = "Projects",
            roomId = RoomId("!projects:example.com"),
            canonicalAlias = RoomAlias("#projects:example.com"),
            level = 1,
        ),
        aSpaceServiceFilter(
            displayName = "Gaming",
            roomId = RoomId("!gaming:example.com"),
        ),
    ),
    searchQuery: String = "",
    eventSink: (SpaceFiltersEvent.Selecting) -> Unit = {},
) = SpaceFiltersState.Selecting(
    availableFilters = availableFilters.toImmutableList(),
    searchQuery = TextFieldState(searchQuery),
    eventSink = eventSink,
)

fun aSelectedSpaceFiltersState(
    selectedFilter: SpaceServiceFilter = aSpaceServiceFilter(displayName = "Work"),
    eventSink: (SpaceFiltersEvent.Selected) -> Unit = {},
) = SpaceFiltersState.Selected(
    selectedFilter = selectedFilter,
    eventSink = eventSink,
)

fun aSpaceServiceFilter(
    displayName: String = "Space",
    roomId: RoomId = RoomId("!space:example.com"),
    canonicalAlias: RoomAlias? = null,
    level: Int = 0,
    descendants: List<RoomId> = emptyList(),
) = SpaceServiceFilter(
    spaceRoom = aSpaceRoom(displayName = displayName, roomId = roomId, canonicalAlias = canonicalAlias),
    level = level,
    descendants = descendants,
)
