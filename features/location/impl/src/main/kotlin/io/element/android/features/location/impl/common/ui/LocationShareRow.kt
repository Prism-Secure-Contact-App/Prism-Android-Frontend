/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.location.api.Location
import io.prism.android.features.location.impl.show.LocationShareItem
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.IconButton
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.room.location.AssetType
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun LocationShareRow(
    item: LocationShareItem,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            avatarData = item.avatarData,
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = item.displayName,
                style = PRISMTheme.typography.fontBodyLgMedium,
                color = PRISMTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (item.isLive) {
                    Icon(
                        imageVector = CompoundIcons.LocationPinSolid(),
                        contentDescription = null,
                        tint = PRISMTheme.colors.iconAccentPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    val icon = if (item.assetType == AssetType.PIN) {
                        CompoundIcons.LocationNavigator()
                    } else {
                        CompoundIcons.LocationNavigatorCentred()
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PRISMTheme.colors.iconSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = item.formattedTimestamp,
                    style = PRISMTheme.typography.fontBodySmRegular,
                    color = PRISMTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = CompoundIcons.ShareAndroid(),
                contentDescription = stringResource(CommonStrings.action_share),
                tint = PRISMTheme.colors.iconPrimary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LocationShareRowPreview() = PRISMPreview {
    Column {
        LocationShareRow(
            item = LocationShareItem(
                userId = UserId("@alice:prism.org"),
                displayName = "Alice",
                avatarData = AvatarData(
                    id = "@alice:prism.org",
                    name = "Alice",
                    url = null,
                    size = AvatarSize.UserListItem,
                ),
                formattedTimestamp = "Shared 1 min ago",
                isLive = true,
                assetType = AssetType.SENDER,
                location = Location(0.0, 0.0)
            ),
            onShareClick = {},
        )
        LocationShareRow(
            item = LocationShareItem(
                userId = UserId("@bob:prism.org"),
                displayName = "Bob",
                avatarData = AvatarData(
                    id = "@bob:prism.org",
                    name = "Bob",
                    url = null,
                    size = AvatarSize.UserListItem,
                ),
                isLive = false,
                assetType = AssetType.PIN,
                formattedTimestamp = "Shared 5 hours ago",
                location = Location(0.0, 0.0)
            ),
            onShareClick = {},
        )
    }
}
