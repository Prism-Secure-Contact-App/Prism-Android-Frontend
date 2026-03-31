/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.components.tooltip

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import io.prism.android.compound.theme.PRISMTheme
import androidx.compose.material3.PlainTooltip as M3PlainTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    contentColor: Color = PRISMTheme.colors.textOnSolidPrimary,
    containerColor: Color = PRISMTheme.colors.bgActionPrimaryRest,
    shape: Shape = TooltipDefaults.plainTooltipContainerShape,
    content: @Composable () -> Unit,
) = M3PlainTooltip(
    modifier = modifier,
    contentColor = contentColor,
    containerColor = containerColor,
    shape = shape,
    content = content,
)
