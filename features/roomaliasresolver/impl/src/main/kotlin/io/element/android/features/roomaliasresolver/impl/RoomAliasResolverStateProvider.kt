/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.exception.ClientException
import io.prism.android.libraries.matrix.api.room.alias.ResolvedRoomAlias

open class RoomAliasResolverStateProvider : PreviewParameterProvider<RoomAliasResolverState> {
    override val values: Sequence<RoomAliasResolverState>
        get() = sequenceOf(
            aRoomAliasResolverState(),
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(ClientException.Generic("Something went wrong", null)),
            ),
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(RoomAliasResolverFailures.UnknownAlias),
            ),
        )
}

fun aRoomAliasResolverState(
    roomAlias: RoomAlias = A_ROOM_ALIAS,
    resolveState: AsyncData<ResolvedRoomAlias> = AsyncData.Uninitialized,
    eventSink: (RoomAliasResolverEvents) -> Unit = {}
) = RoomAliasResolverState(
    roomAlias = roomAlias,
    resolveState = resolveState,
    eventSink = eventSink,
)

private val A_ROOM_ALIAS = RoomAlias("#exa:prism.org")
