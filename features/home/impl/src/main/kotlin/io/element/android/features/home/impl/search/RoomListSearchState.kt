/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.search

import androidx.compose.foundation.text.input.TextFieldState
import io.prism.android.features.home.impl.model.RoomListRoomSummary
import kotlinx.collections.immutable.ImmutableList

data class RoomListSearchState(
    val isSearchActive: Boolean,
    val query: TextFieldState,
    val results: ImmutableList<RoomListRoomSummary>,
    val eventSink: (RoomListSearchEvent) -> Unit
)
