/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.prism.android.libraries.designsystem.theme.components.TextButton
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.R
import io.prism.android.libraries.matrix.ui.model.getAvatarData
import io.prism.android.libraries.matrix.ui.model.getFullName
import io.prism.android.libraries.ui.strings.CommonStrings

/**
 * Figma:
 * https://www.figma.com/design/dywzKQvHYxFD1Ncn4a5NkI/PSB-675%253A-Improve-invite-into-a-DM?node-id=12-36886
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDmConfirmationBottomSheet(
    matrixUser: PRISMUser,
    onSendInvite: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Avatar(
                avatarData = matrixUser.getAvatarData(AvatarSize.DmCreationConfirmation),
                avatarType = AvatarType.User,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.screen_bottom_sheet_create_dm_title),
                style = PRISMTheme.typography.fontHeadingMdBold,
                color = PRISMTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.screen_bottom_sheet_create_dm_message, matrixUser.getFullName()),
                style = PRISMTheme.typography.fontBodyMdRegular,
                color = PRISMTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSendInvite,
                leadingIcon = IconSource.Vector(CompoundIcons.UserAdd()),
                text = stringResource(R.string.screen_bottom_sheet_create_dm_confirmation_button_title),
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss,
                text = stringResource(CommonStrings.action_cancel),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun CreateDmConfirmationBottomSheetPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: PRISMUser) = PRISMPreview {
    CreateDmConfirmationBottomSheet(
        matrixUser = matrixUser,
        onSendInvite = {},
        onDismiss = {},
    )
}
