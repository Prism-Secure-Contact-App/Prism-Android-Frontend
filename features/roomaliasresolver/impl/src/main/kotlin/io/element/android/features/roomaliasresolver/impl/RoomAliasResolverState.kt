/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl

import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.room.alias.ResolvedRoomAlias

data class RoomAliasResolverState(
    val roomAlias: RoomAlias,
    val resolveState: AsyncData<ResolvedRoomAlias>,
    val eventSink: (RoomAliasResolverEvents) -> Unit
)

sealed class RoomAliasResolverFailures : Exception() {
    data object UnknownAlias : RoomAliasResolverFailures()
}
