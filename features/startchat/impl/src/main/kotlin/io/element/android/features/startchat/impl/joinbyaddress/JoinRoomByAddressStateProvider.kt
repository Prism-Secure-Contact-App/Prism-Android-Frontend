/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.joinbyaddress

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.alias.ResolvedRoomAlias

open class JoinRoomByAddressStateProvider : PreviewParameterProvider<JoinRoomByAddressState> {
    override val values: Sequence<JoinRoomByAddressState>
        get() = sequenceOf(
            aJoinRoomByAddressState(),
            aJoinRoomByAddressState(address = "#room-"),
            aJoinRoomByAddressState(address = "#room-", addressState = RoomAddressState.Invalid),
            aJoinRoomByAddressState(address = "#room-name:prism.org", addressState = RoomAddressState.Resolving),
            aJoinRoomByAddressState(address = "#room-name-none:prism.org", addressState = RoomAddressState.RoomNotFound),
            aJoinRoomByAddressState(
                address = "#room-name:prism.org",
                addressState = RoomAddressState.RoomFound(ResolvedRoomAlias(RoomId("!aRoom:id"), emptyList())),
            ),
        )
}

fun aJoinRoomByAddressState(
    address: String = "",
    addressState: RoomAddressState = RoomAddressState.Unknown,
    eventSink: (JoinRoomByAddressEvent) -> Unit = {},
) = JoinRoomByAddressState(
    address = address,
    addressState = addressState,
    eventSink = eventSink
)
