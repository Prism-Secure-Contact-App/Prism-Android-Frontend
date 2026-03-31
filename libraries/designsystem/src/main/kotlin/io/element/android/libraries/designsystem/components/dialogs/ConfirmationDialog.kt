/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.components.dialogs

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PRISMThemedPreview
import io.prism.android.libraries.designsystem.preview.PreviewGroup
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.DialogPreview
import io.prism.android.libraries.designsystem.theme.components.SimpleAlertDialogContent
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    content: String,
    onSubmitClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    submitText: String = stringResource(id = CommonStrings.action_ok),
    cancelText: String = stringResource(id = CommonStrings.action_cancel),
    destructiveSubmit: Boolean = false,
    thirdButtonText: String? = null,
    onCancelClick: () -> Unit = onDismiss,
    onThirdButtonClick: () -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
) {
    BasicAlertDialog(modifier = modifier, onDismissRequest = onDismiss) {
        ConfirmationDialogContent(
            title = title,
            content = content,
            submitText = submitText,
            cancelText = cancelText,
            thirdButtonText = thirdButtonText,
            destructiveSubmit = destructiveSubmit,
            onSubmitClick = onSubmitClick,
            onCancelClick = onCancelClick,
            onThirdButtonClick = onThirdButtonClick,
            icon = icon,
        )
    }
}

@Composable
private fun ConfirmationDialogContent(
    content: String,
    submitText: String,
    cancelText: String,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit,
    title: String? = null,
    thirdButtonText: String? = null,
    onThirdButtonClick: () -> Unit = {},
    destructiveSubmit: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
) {
    SimpleAlertDialogContent(
        title = title,
        content = content,
        submitText = submitText,
        onSubmitClick = onSubmitClick,
        cancelText = cancelText,
        onCancelClick = onCancelClick,
        thirdButtonText = thirdButtonText,
        onThirdButtonClick = onThirdButtonClick,
        destructiveSubmit = destructiveSubmit,
        icon = icon,
    )
}

@Preview(group = PreviewGroup.Dialogs)
@Composable
internal fun ConfirmationDialogContentPreview() =
    PRISMThemedPreview(showBackground = false) {
        DialogPreview {
            ConfirmationDialogContent(
                content = "Content",
                title = "Title",
                submitText = "OK",
                cancelText = "Cancel",
                thirdButtonText = "Disable",
                onSubmitClick = {},
                onCancelClick = {},
                onThirdButtonClick = {},
            )
        }
    }

@PreviewsDayNight
@Composable
internal fun ConfirmationDialogPreview() = PRISMPreview {
    ConfirmationDialog(
        content = "Content",
        title = "Title",
        submitText = "OK",
        cancelText = "Cancel",
        thirdButtonText = "Disable",
        onSubmitClick = {},
        onDismiss = {}
    )
}
