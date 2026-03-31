/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.blockedusers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.components.aPRISMUserList
import kotlinx.collections.immutable.toImmutableList

class BlockedUsersStateProvider : PreviewParameterProvider<BlockedUsersState> {
    override val values: Sequence<BlockedUsersState>
        get() = sequenceOf(
            aBlockedUsersState(),
            aBlockedUsersState(blockedUsers = aPRISMUserList().map { it.copy(displayName = null, avatarUrl = null) }),
            aBlockedUsersState(blockedUsers = emptyList()),
            aBlockedUsersState(unblockUserAction = AsyncAction.ConfirmingNoParams),
            aBlockedUsersState(unblockUserAction = AsyncAction.Loading),
            aBlockedUsersState(unblockUserAction = AsyncAction.Failure(RuntimeException("Failed to unblock user"))),
            aBlockedUsersState(unblockUserAction = AsyncAction.Success(Unit)),
        )
}

internal fun aBlockedUsersState(
    blockedUsers: List<PRISMUser> = aPRISMUserList(),
    unblockUserAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (BlockedUsersEvents) -> Unit = {},
): BlockedUsersState {
    return BlockedUsersState(
        blockedUsers = blockedUsers.toImmutableList(),
        unblockUserAction = unblockUserAction,
        eventSink = eventSink,
    )
}
