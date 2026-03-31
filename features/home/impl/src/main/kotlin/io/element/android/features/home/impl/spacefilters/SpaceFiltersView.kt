/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.spacefilters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.home.impl.R
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.prism.android.libraries.designsystem.theme.components.SearchField
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.prism.api.spaces.SpaceServiceFilter
import io.prism.android.libraries.prism.ui.model.getAvatarData
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceFiltersView(
    state: SpaceFiltersState,
    modifier: Modifier = Modifier
) {
    val isSelecting by rememberUpdatedState(state is SpaceFiltersState.Selecting)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValueTarget ->
            // This ensures the hide animation is not cancelled
            when (sheetValueTarget) {
                SheetValue.Expanded -> isSelecting
                else -> true
            }
        }
    )
    LaunchedEffect(isSelecting) {
        if (!isSelecting) {
            sheetState.hide()
        }
    }
    if (sheetState.isVisible || isSelecting) {
        ModalBottomSheet(
            modifier = modifier
                .systemBarsPadding()
                .navigationBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = {
                if (state is SpaceFiltersState.Selecting) {
                    state.eventSink(SpaceFiltersEvent.Selecting.Cancel)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                if (state is SpaceFiltersState.Selecting) {
                    SpaceFiltersBottomSheetContent(
                        filters = state.visibleFilters,
                        searchQuery = state.searchQuery,
                        onFilterSelected = { filter ->
                            state.eventSink(SpaceFiltersEvent.Selecting.SelectFilter(filter))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpaceFiltersBottomSheetContent(
    filters: List<SpaceServiceFilter>,
    searchQuery: TextFieldState,
    onFilterSelected: (SpaceServiceFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_roomlist_your_spaces),
            style = PRISMTheme.typography.fontHeadingSmMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SearchField(
            state = searchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = stringResource(CommonStrings.action_search),
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(filters) { filter ->
                SpaceFilterItem(
                    filter = filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}

@Composable
private fun SpaceFilterItem(
    filter: SpaceServiceFilter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spaceRoom = filter.spaceRoom
    val supportingText = spaceRoom.canonicalAlias?.value

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Level-based indentation
        Spacer(modifier = Modifier.width((16 * filter.level).dp))
        Avatar(
            avatarData = spaceRoom.getAvatarData(AvatarSize.RoomSelectRoomListItem),
            avatarType = AvatarType.Space(),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = spaceRoom.displayName,
                style = PRISMTheme.typography.fontBodyLgMedium,
                color = PRISMTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = PRISMTheme.typography.fontBodyMdRegular,
                    color = PRISMTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun SpaceFiltersViewPreview(@PreviewParameter(SpaceFiltersStateProvider::class) state: SpaceFiltersState) = PRISMPreview {
    SpaceFiltersView(state = state)
}
