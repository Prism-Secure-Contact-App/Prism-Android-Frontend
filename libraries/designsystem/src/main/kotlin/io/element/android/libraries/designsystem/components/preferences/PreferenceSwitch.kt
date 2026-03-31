/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.components.preferences

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.components.preferences.components.preferenceIcon
import io.prism.android.libraries.designsystem.preview.PRISMThemedPreview
import io.prism.android.libraries.designsystem.preview.PreviewGroup
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
fun PreferenceSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    @DrawableRes iconResourceId: Int? = null,
    showIconAreaIfNoIcon: Boolean = false,
) {
    ListItem(
        modifier = modifier,
        enabled = enabled,
        onClick = onCheckedChange.takeIf { enabled }?.let { { onCheckedChange(!isChecked) } },
        leadingContent = preferenceIcon(
            icon = icon,
            iconResourceId = iconResourceId,
            showIconAreaIfNoIcon = showIconAreaIfNoIcon,
        ),
        headlineContent = {
            Text(
                style = PRISMTheme.typography.fontBodyLgRegular,
                text = title,
            )
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    style = PRISMTheme.typography.fontBodyMdRegular,
                    text = subtitle,
                )
            }
        },
        trailingContent = ListItemContent.Switch(
            checked = isChecked,
            enabled = enabled,
        )
    )
}

@Preview(group = PreviewGroup.Preferences)
@Composable
internal fun PreferenceSwitchPreview() = PRISMThemedPreview {
    Column {
        PreferenceSwitch(
            title = "Switch",
            subtitle = "Subtitle Switch",
            icon = CompoundIcons.Threads(),
            enabled = true,
            isChecked = true,
            onCheckedChange = {},
        )
        PreferenceSwitch(
            title = "Switch",
            subtitle = "Subtitle Switch",
            icon = CompoundIcons.Threads(),
            enabled = false,
            isChecked = true,
            onCheckedChange = {},
        )
        PreferenceSwitch(
            title = "Switch no subtitle",
            subtitle = null,
            icon = CompoundIcons.Threads(),
            enabled = false,
            isChecked = true,
            onCheckedChange = {},
        )
    }
}
