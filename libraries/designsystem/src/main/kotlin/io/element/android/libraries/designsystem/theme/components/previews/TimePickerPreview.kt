/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.theme.components.previews

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreviewDark
import io.prism.android.libraries.designsystem.preview.PRISMPreviewLight
import io.prism.android.libraries.designsystem.preview.PRISMThemedPreview
import io.prism.android.libraries.designsystem.preview.PreviewGroup
import io.prism.android.libraries.designsystem.theme.components.AlertDialogContent

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 600, group = PreviewGroup.DateTimePickers)
@Composable
internal fun TimePickerHorizontalPreview() {
    PRISMThemedPreview {
        AlertDialogContent(
            buttons = { /*TODO*/ },
            icon = { /*TODO*/ },
            title = { /*TODO*/ },
            subtitle = null,
            content = { TimePicker(state = rememberTimePickerState(), layoutType = TimePickerLayoutType.Horizontal) },
            shape = AlertDialogDefaults.shape,
            containerColor = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            buttonContentColor = PRISMTheme.colors.textPrimary,
            iconContentColor = AlertDialogDefaults.iconContentColor,
            titleContentColor = AlertDialogDefaults.titleContentColor,
            textContentColor = AlertDialogDefaults.textContentColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = PreviewGroup.DateTimePickers)
@Composable
internal fun TimePickerVerticalLightPreview() {
    PRISMPreviewLight {
        AlertDialogContent(
            buttons = { /*TODO*/ },
            icon = { /*TODO*/ },
            title = { /*TODO*/ },
            subtitle = null,
            content = { TimePicker(state = rememberTimePickerState(), layoutType = TimePickerLayoutType.Vertical) },
            shape = AlertDialogDefaults.shape,
            containerColor = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            buttonContentColor = PRISMTheme.colors.textPrimary,
            iconContentColor = AlertDialogDefaults.iconContentColor,
            titleContentColor = AlertDialogDefaults.titleContentColor,
            textContentColor = AlertDialogDefaults.textContentColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = PreviewGroup.DateTimePickers)
@Composable
internal fun TimePickerVerticalDarkPreview() {
    val pickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
    )
    PRISMPreviewDark {
        AlertDialogContent(
            buttons = { /*TODO*/ },
            icon = { /*TODO*/ },
            title = { /*TODO*/ },
            subtitle = null,
            content = { TimePicker(state = pickerState, layoutType = TimePickerLayoutType.Vertical) },
            shape = AlertDialogDefaults.shape,
            containerColor = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            buttonContentColor = PRISMTheme.colors.textPrimary,
            iconContentColor = AlertDialogDefaults.iconContentColor,
            titleContentColor = AlertDialogDefaults.titleContentColor,
            textContentColor = AlertDialogDefaults.textContentColor,
        )
    }
}
