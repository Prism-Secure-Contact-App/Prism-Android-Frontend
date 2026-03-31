/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.topbars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.components.avatar.anAvatarData
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.TopAppBar
import io.prism.android.libraries.prism.ui.components.aPRISMUserList
import io.prism.android.libraries.prism.ui.model.getAvatarData
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThreadTopBar(
    roomName: String?,
    roomAvatarData: AvatarData,
    heroes: ImmutableList<AvatarData>,
    isTombstoned: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    avatarData = roomAvatarData,
                    avatarType = AvatarType.Room(
                        heroes = heroes,
                        isTombstoned = isTombstoned,
                    ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .semantics {
                            heading()
                        },
                ) {
                    Text(
                        text = stringResource(CommonStrings.common_thread),
                        style = PRISMTheme.typography.fontBodyLgMedium,
                    )
                    Text(
                        text = roomName ?: stringResource(CommonStrings.common_no_room_name),
                        style = PRISMTheme.typography.fontBodySmRegular,
                        fontStyle = FontStyle.Italic.takeIf { roomName == null },
                        color = PRISMTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}

@PreviewsDayNight
@Composable
internal fun ThreadTopBarPreview() = PRISMPreview {
    @Composable
    fun AThreadTopBar(
        roomName: String? = "Room name",
        roomAvatarData: AvatarData = anAvatarData(
            name = "Room name",
            size = AvatarSize.TimelineRoom,
        ),
        isTombstoned: Boolean = false,
        heroes: ImmutableList<AvatarData> = persistentListOf(),
    ) = ThreadTopBar(
        roomName = roomName,
        roomAvatarData = roomAvatarData,
        isTombstoned = isTombstoned,
        heroes = heroes,
        onBackClick = {},
    )
    Column {
        AThreadTopBar()
        HorizontalDivider()
        AThreadTopBar(
            heroes = aPRISMUserList().map { it.getAvatarData(AvatarSize.TimelineRoom) }.toImmutableList(),
        )
        HorizontalDivider()
        AThreadTopBar(
            roomName = null,
        )
        HorizontalDivider()
        AThreadTopBar(
            roomAvatarData = anAvatarData(
                name = "Room name",
                url = "https://some-avatar.jpg",
                size = AvatarSize.TimelineRoom,
            ),
        )
        HorizontalDivider()
        AThreadTopBar(
            isTombstoned = true,
        )
    }
}
