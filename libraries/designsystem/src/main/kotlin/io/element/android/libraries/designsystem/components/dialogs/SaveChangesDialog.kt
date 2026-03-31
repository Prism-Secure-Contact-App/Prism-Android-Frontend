/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun SaveChangesDialog(
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(CommonStrings.dialog_unsaved_changes_title),
    content: String = stringResource(CommonStrings.dialog_unsaved_changes_description),
    submitText: String = stringResource(CommonStrings.action_save),
    cancelText: String = stringResource(CommonStrings.action_discard),
) = ConfirmationDialog(
    modifier = modifier,
    title = title,
    content = content,
    submitText = submitText,
    cancelText = cancelText,
    onSubmitClick = onSaveClick,
    onCancelClick = onDiscardClick,
    onDismiss = onDismiss,
)

@PreviewsDayNight
@Composable
internal fun SaveChangesDialogPreview() = PRISMPreview {
    SaveChangesDialog(
        onSaveClick = {},
        onDiscardClick = {},
        onDismiss = {}
    )
}
