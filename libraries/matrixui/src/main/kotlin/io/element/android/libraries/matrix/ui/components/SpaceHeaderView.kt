/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import io.prism.android.libraries.designsystem.atomic.atoms.RoomPreviewAliasAtom
import io.prism.android.libraries.designsystem.atomic.atoms.RoomPreviewDescriptionAtom
import io.prism.android.libraries.designsystem.atomic.atoms.RoomPreviewTitleAtom
import io.prism.android.libraries.designsystem.atomic.organisms.RoomPreviewOrganism
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.components.avatar.anAvatarData
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.matrix.api.core.RoomAlias
import io.prism.android.libraries.matrix.api.spaces.SpaceRoomVisibility
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Ref: https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=3643-2429&m=dev
 */
@Composable
fun SpaceHeaderView(
    avatarData: AvatarData,
    name: String?,
    alias: RoomAlias?,
    topic: String?,
    visibility: SpaceRoomVisibility,
    heroes: ImmutableList<PRISMUser>,
    numberOfMembers: Int,
    modifier: Modifier = Modifier,
    topicMaxLines: Int = Int.MAX_VALUE,
    onTopicClick: ((String) -> Unit)? = null,
) {
    RoomPreviewOrganism(
        modifier = modifier.padding(24.dp),
        avatar = {
            Avatar(
                avatarData = avatarData,
                avatarType = AvatarType.Space(),
            )
        },
        title = {
            if (name != null) {
                RoomPreviewTitleAtom(title = name)
            } else {
                RoomPreviewTitleAtom(
                    title = stringResource(id = CommonStrings.common_no_space_name),
                    fontStyle = FontStyle.Italic
                )
            }
        },
        subtitle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (alias != null) {
                    RoomPreviewAliasAtom(alias = alias.value)
                }
                SpaceInfoRow(visibility = visibility)
            }
        },
        description = if (topic.isNullOrBlank()) {
            null
        } else {
            {
                RoomPreviewDescriptionAtom(
                    description = topic,
                    maxLines = topicMaxLines,
                    modifier = Modifier.clickable(
                        enabled = onTopicClick != null,
                        onClick = { onTopicClick?.invoke(topic) }
                    )
                )
            }
        },
        memberCount = {
            SpaceMembersView(
                heroes = heroes,
                numberOfMembers = numberOfMembers,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        },
    )
}

@PreviewsDayNight
@Composable
internal fun SpaceHeaderViewPreview() = PRISMPreview {
    SpaceHeaderView(
        avatarData = anAvatarData(
            url = "anUrl",
            size = AvatarSize.SpaceHeader,
        ),
        alias = RoomAlias("#spaceAlias:matrix.org"),
        name = "Space name",
        topic = "Space topic: " + LoremIpsum(40).values.first(),
        topicMaxLines = 2,
        visibility = SpaceRoomVisibility.Public,
        heroes = persistentListOf(
            aMatrixUser(id = "@1:d", displayName = "Alice", avatarUrl = "aUrl"),
            aMatrixUser(id = "@2:d", displayName = "Bob"),
            aMatrixUser(id = "@3:d", displayName = "Charlie", avatarUrl = "aUrl"),
            aMatrixUser(id = "@4:d", displayName = "Dave"),
        ),
        numberOfMembers = 999,
    )
}
