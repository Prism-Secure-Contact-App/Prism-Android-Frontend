/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.impl.addroom

import androidx.compose.foundation.text.input.TextFieldState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.ui.model.SelectRoomInfo
import kotlinx.collections.immutable.ImmutableList

data class AddRoomToSpaceState(
    val searchQuery: TextFieldState,
    val isSearchActive: Boolean,
    val searchResults: SearchBarResultState<ImmutableList<SelectRoomInfo>>,
    val selectedRooms: ImmutableList<SelectRoomInfo>,
    val suggestions: ImmutableList<SelectRoomInfo>,
    val saveAction: AsyncAction<Unit>,
    val eventSink: (AddRoomToSpaceEvent) -> Unit,
) {
    val canSave: Boolean = selectedRooms.isNotEmpty() && !saveAction.isLoading()
}
