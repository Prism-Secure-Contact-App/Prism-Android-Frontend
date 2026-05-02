/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.model.getAvatarData
import io.prism.android.libraries.matrix.ui.model.getBestName

@Composable
fun MatrixUserRow(
    matrixUser: PRISMUser,
    modifier: Modifier = Modifier,
    avatarSize: AvatarSize = AvatarSize.UserListItem,
    trailingContent: @Composable (() -> Unit)? = null,
) = UserRow(
    avatarData = matrixUser.getAvatarData(avatarSize),
    name = matrixUser.getBestName(),
    subtext = if (matrixUser.displayName.isNullOrEmpty()) null else matrixUser.userId.value,
    modifier = modifier,
    trailingContent = trailingContent,
)

@PreviewsDayNight
@Composable
internal fun MatrixUserRowPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: PRISMUser) = PRISMPreview {
    MatrixUserRow(matrixUser)
}
