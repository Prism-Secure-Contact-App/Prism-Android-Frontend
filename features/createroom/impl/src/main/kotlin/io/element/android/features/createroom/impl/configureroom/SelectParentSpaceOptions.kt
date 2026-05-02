/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.impl.configureroom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.createroom.impl.R
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.ListSectionHeader
import io.prism.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.hide
import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.spaces.SpaceRoom
import io.prism.android.libraries.previewutils.room.aSpaceRoom
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectParentSpaceOptions(
    spaces: ImmutableList<SpaceRoom>,
    selectedSpace: SpaceRoom?,
    onSelectSpace: (SpaceRoom?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    var displaySelectSpaceBottomSheet by remember { mutableStateOf(false) }
    ConfigureRoomOptions(
        title = stringResource(CommonStrings.common_space),
        hasDivider = false,
        modifier = modifier
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = selectedSpace?.displayName
                        ?: stringResource(R.string.screen_create_room_space_selection_no_space_title),
                    maxLines = 1,
                    color = PRISMTheme.colors.textPrimary
                )
            },
            supportingContent = selectedSpace?.canonicalAlias?.let { alias ->
                {
                    Text(text = alias.value, maxLines = 1)
                }
            },
            leadingContent = selectedSpace?.let {
                ListItemContent.Custom({
                    val avatarData = AvatarData(
                        id = selectedSpace.roomId.value,
                        name = selectedSpace.displayName,
                        url = selectedSpace.avatarUrl,
                        size = AvatarSize.SelectParentSpace,
                    )
                    Avatar(avatarData = avatarData, avatarType = AvatarType.Space())
                })
            },
            onClick = { displaySelectSpaceBottomSheet = true }
        )

        if (displaySelectSpaceBottomSheet) {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { true },
            )
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    sheetState.hide(coroutineScope) {
                        displaySelectSpaceBottomSheet = false
                    }
                }
            ) {
                SelectParentSpaceBottomSheet(
                    spaces = spaces,
                    selectedSpace = selectedSpace,
                ) {
                    sheetState.hide(coroutineScope) {
                        displaySelectSpaceBottomSheet = false
                    }
                    onSelectSpace(it)
                }
            }
        }
    }
}

@Composable
private fun SelectParentSpaceBottomSheet(
    spaces: ImmutableList<SpaceRoom>,
    selectedSpace: SpaceRoom?,
    onSelectSpace: (SpaceRoom?) -> Unit,
) {
    ListSectionHeader(
        title = stringResource(R.string.screen_create_room_space_selection_sheet_title),
        hasDivider = false
    )
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.screen_create_room_space_selection_no_space_option),
                        maxLines = 1
                    )
                },
                trailingContent = ListItemContent.RadioButton(
                    selected = selectedSpace == null
                ),
                onClick = { onSelectSpace(null) },
            )
        }
        for (space in spaces) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = space.displayName,
                            maxLines = 1
                        )
                    },
                    supportingContent = space.canonicalAlias?.let { alias ->
                        {
                            Text(
                                text = alias.value,
                                maxLines = 1
                            )
                        }
                    },
                    leadingContent = ListItemContent.Custom({
                        val avatarData =
                            AvatarData(
                                id = space.roomId.value,
                                name = space.displayName,
                                url = space.avatarUrl,
                                size = AvatarSize.SelectParentSpace,
                            )
                        Avatar(
                            avatarData = avatarData,
                            avatarType = AvatarType.Space()
                        )
                    }),
                    trailingContent = ListItemContent.RadioButton(
                        selected = selectedSpace == space
                    ),
                    onClick = { onSelectSpace(space) },
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun SelectParentSpaceBottomSheetPreview() =
    PRISMPreview {
        Column {
            SelectParentSpaceBottomSheet(
                spaces = persistentListOf(
                    aSpaceRoom(
                        canonicalAlias = RoomAlias(
                            "#a-room-alias:example.org"
                        )
                    ),
                    aSpaceRoom()
                ),
                selectedSpace = null,
            ) {}
        }
    }
