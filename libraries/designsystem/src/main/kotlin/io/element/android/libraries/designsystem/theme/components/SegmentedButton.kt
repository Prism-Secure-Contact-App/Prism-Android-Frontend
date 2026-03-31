/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.theme.PRISMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceSegmentedButtonRowScope.SegmentedButton(
    index: Int,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    SegmentedButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = enabled,
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
        label = {
            Text(
                text = text,
                style = PRISMTheme.typography.fontBodyMdMedium,
            )
        },
        colors = SegmentedButtonDefaults.colors(
            activeContainerColor = PRISMTheme.materialColors.primary,
            activeContentColor = PRISMTheme.materialColors.onPrimary,
            activeBorderColor = PRISMTheme.materialColors.primary,
            inactiveContainerColor = PRISMTheme.materialColors.surface,
            inactiveContentColor = PRISMTheme.materialColors.onSurface,
            inactiveBorderColor = PRISMTheme.materialColors.primary,
            disabledActiveContainerColor = PRISMTheme.colors.bgActionPrimaryDisabled,
            disabledActiveContentColor = PRISMTheme.colors.textOnSolidPrimary,
            disabledActiveBorderColor = PRISMTheme.colors.bgActionPrimaryDisabled,
            disabledInactiveContainerColor = PRISMTheme.materialColors.surface,
            disabledInactiveContentColor = PRISMTheme.colors.textDisabled,
            disabledInactiveBorderColor = Color.Transparent,
        )
    )
}
