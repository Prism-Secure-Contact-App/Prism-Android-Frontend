/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdirectory.impl.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.roomdirectory.api.RoomDescription
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

open class RoomDirectoryStateProvider : PreviewParameterProvider<RoomDirectoryState> {
    override val values: Sequence<RoomDirectoryState>
        get() = sequenceOf(
            aRoomDirectoryState(),
            aRoomDirectoryState(
                query = "PRISM",
                roomDescriptions = aRoomDescriptionList(),
            ),
            aRoomDirectoryState(
                query = "PRISM",
                roomDescriptions = aRoomDescriptionList(),
                displayLoadMoreIndicator = true,
            ),
        )
}

fun aRoomDirectoryState(
    query: String = "",
    displayLoadMoreIndicator: Boolean = false,
    roomDescriptions: ImmutableList<RoomDescription> = persistentListOf(),
    eventSink: (RoomDirectoryEvents) -> Unit = {},
) = RoomDirectoryState(
    query = query,
    roomDescriptions = roomDescriptions,
    displayLoadMoreIndicator = displayLoadMoreIndicator,
    eventSink = eventSink,
)

fun aRoomDescriptionList(): ImmutableList<RoomDescription> {
    return persistentListOf(
        RoomDescription(
            roomId = RoomId("!exa:prism.org"),
            name = "PRISM X Android",
            topic = "PRISM X is a secure, private and decentralized messenger.",
            alias = RoomAlias("#prism-x-android:prism.org"),
            avatarUrl = null,
            joinRule = RoomDescription.JoinRule.PUBLIC,
            numberOfMembers = 2765,
        ),
        RoomDescription(
            roomId = RoomId("!exi:prism.org"),
            name = "PRISM X iOS",
            topic = "PRISM X is a secure, private and decentralized messenger.",
            alias = RoomAlias("#prism-x-ios:prism.org"),
            avatarUrl = null,
            joinRule = RoomDescription.JoinRule.UNKNOWN,
            numberOfMembers = 356,
        )
    )
}
