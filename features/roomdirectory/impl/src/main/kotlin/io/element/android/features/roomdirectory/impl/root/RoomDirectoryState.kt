/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdirectory.impl.root

import io.prism.android.features.roomdirectory.api.RoomDescription
import kotlinx.collections.immutable.ImmutableList

data class RoomDirectoryState(
    val query: String,
    val roomDescriptions: ImmutableList<RoomDescription>,
    val displayLoadMoreIndicator: Boolean,
    val eventSink: (RoomDirectoryEvents) -> Unit
) {
    val displayEmptyState = roomDescriptions.isEmpty() && !displayLoadMoreIndicator
}
