/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.theme.PRISMTheme

@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = PRISMNavigationBarItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = colors,
        interactionSource = interactionSource,
    )
}

object PRISMNavigationBarItemDefaults {
    @Composable
    fun colors() = NavigationBarItemDefaults.colors().copy(
        selectedIconColor = PRISMTheme.colors.iconPrimary,
        selectedTextColor = PRISMTheme.colors.textPrimary,
        unselectedIconColor = PRISMTheme.colors.iconTertiary,
        unselectedTextColor = PRISMTheme.colors.textDisabled,
        selectedIndicatorColor = Color.Transparent,
    )
}
