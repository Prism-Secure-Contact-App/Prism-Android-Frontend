/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
internal fun UserRow(
    avatarData: AvatarData,
    name: String,
    subtext: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarData = avatarData,
            avatarType = AvatarType.User,
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            // Name
            Text(
                modifier = Modifier.clipToBounds(),
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (enabled) PRISMTheme.colors.textPrimary else PRISMTheme.colors.textDisabled,
                style = PRISMTheme.typography.fontBodyLgRegular,
            )
            // Id
            subtext?.let {
                Text(
                    text = subtext,
                    color = if (enabled) PRISMTheme.colors.textSecondary else PRISMTheme.colors.textDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = PRISMTheme.typography.fontBodySmRegular,
                )
            }
        }
        trailingContent?.invoke()
    }
}
