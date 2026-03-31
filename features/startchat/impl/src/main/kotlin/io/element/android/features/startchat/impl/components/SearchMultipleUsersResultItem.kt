/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.preview.PRISMThemedPreview
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.prism.ui.components.CheckableUserRow
import io.prism.android.libraries.prism.ui.components.CheckableUserRowData
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.prism.ui.model.getAvatarData
import io.prism.android.libraries.prism.ui.model.getBestName
import io.prism.android.libraries.usersearch.api.UserSearchResult

@Composable
fun SearchMultipleUsersResultItem(
    searchResult: UserSearchResult,
    isUserSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val data = if (searchResult.isUnresolved) {
        CheckableUserRowData.Unresolved(
            avatarData = searchResult.prismUser.getAvatarData(AvatarSize.UserListItem),
            id = searchResult.prismUser.userId.value,
        )
    } else {
        CheckableUserRowData.Resolved(
            name = searchResult.prismUser.getBestName(),
            subtext = if (searchResult.prismUser.displayName.isNullOrEmpty()) null else searchResult.prismUser.userId.value,
            avatarData = searchResult.prismUser.getAvatarData(AvatarSize.UserListItem),
        )
    }
    CheckableUserRow(
        checked = isUserSelected,
        modifier = modifier,
        data = data,
        onCheckedChange = onCheckedChange,
    )
}

@Preview
@Composable
internal fun SearchMultipleUsersResultItemPreview() = PRISMThemedPreview {
    Column {
        SearchMultipleUsersResultItem(
            searchResult = UserSearchResult(
                aPRISMUser(),
                isUnresolved = false
            ),
            isUserSelected = false,
            onCheckedChange = {}
        )
        HorizontalDivider()
        SearchMultipleUsersResultItem(
            searchResult = UserSearchResult(
                aPRISMUser(),
                isUnresolved = false
            ),
            isUserSelected = true,
            onCheckedChange = {}
        )
        HorizontalDivider()
        SearchMultipleUsersResultItem(
            searchResult = UserSearchResult(
                aPRISMUser(),
                isUnresolved = true
            ),
            isUserSelected = false,
            onCheckedChange = {}
        )
        HorizontalDivider()
        SearchMultipleUsersResultItem(
            searchResult = UserSearchResult(
                aPRISMUser(),
                isUnresolved = true
            ),
            isUserSelected = true,
            onCheckedChange = {}
        )
    }
}
