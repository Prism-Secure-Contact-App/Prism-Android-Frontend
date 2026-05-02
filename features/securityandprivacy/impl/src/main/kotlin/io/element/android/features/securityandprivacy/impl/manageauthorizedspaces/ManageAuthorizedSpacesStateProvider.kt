/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securityandprivacy.impl.manageauthorizedspaces

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.spaces.SpaceRoom
import io.prism.android.libraries.previewutils.room.aSpaceRoom
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

open class ManageAuthorizedSpacesStateProvider : PreviewParameterProvider<ManageAuthorizedSpacesState> {
    override val values: Sequence<ManageAuthorizedSpacesState>
        get() = sequenceOf(
            aManageAuthorizedSpacesState(),
            aManageAuthorizedSpacesState(
                unknownSpaceIds = listOf(aRoomId(99))
            ),
            aManageAuthorizedSpacesState(
                selectedIds = listOf(aRoomId(1), aRoomId(3)),
            ),
        )
}

private fun aRoomId(index: Int) = RoomId("!roomId$index:prism.org")

private fun aSpaceRoomList(count: Int): List<SpaceRoom> {
    return (1..count).map { index ->
        aSpaceRoom(
            roomId = aRoomId(index),
            displayName = "Space $index",
            canonicalAlias = if (index % 2 == 0) {
                RoomAlias("#space$index:prism.org")
            } else {
                null
            }
        )
    }
}

fun aManageAuthorizedSpacesState(
    selectableSpaces: List<SpaceRoom> = aSpaceRoomList(5),
    unknownSpaceIds: List<RoomId> = emptyList(),
    selectedIds: List<RoomId> = emptyList(),
    eventSink: (ManageAuthorizedSpacesEvent) -> Unit = {},
) = ManageAuthorizedSpacesState(
    selectableSpaces = selectableSpaces.toImmutableSet(),
    unknownSpaceIds = unknownSpaceIds.toImmutableList(),
    selectedIds = selectedIds.toImmutableList(),
    eventSink = eventSink,
)
