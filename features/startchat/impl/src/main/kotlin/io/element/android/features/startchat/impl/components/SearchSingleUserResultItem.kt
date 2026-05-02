/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.preview.PRISMThemedPreview
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.matrix.ui.components.MatrixUserRow
import io.prism.android.libraries.matrix.ui.components.UnresolvedUserRow
import io.prism.android.libraries.matrix.ui.components.aMatrixUser
import io.prism.android.libraries.matrix.ui.model.getAvatarData
import io.prism.android.libraries.usersearch.api.UserSearchResult

@Composable
fun SearchSingleUserResultItem(
    searchResult: UserSearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (searchResult.isUnresolved) {
        UnresolvedUserRow(
            modifier = modifier.clickable(onClick = onClick),
            avatarData = searchResult.matrixUser.getAvatarData(AvatarSize.UserListItem),
            id = searchResult.matrixUser.userId.value,
        )
    } else {
        MatrixUserRow(
            modifier = modifier.clickable(onClick = onClick),
            matrixUser = searchResult.matrixUser,
            avatarSize = AvatarSize.UserListItem,
        )
    }
}

@Preview
@Composable
internal fun SearchSingleUserResultItemPreview() = PRISMThemedPreview {
    Column {
        SearchSingleUserResultItem(
            searchResult = UserSearchResult(aMatrixUser(), isUnresolved = false),
            onClick = {},
        )
        HorizontalDivider()
        SearchSingleUserResultItem(
            searchResult = UserSearchResult(aMatrixUser(), isUnresolved = true),
            onClick = {},
        )
    }
}
