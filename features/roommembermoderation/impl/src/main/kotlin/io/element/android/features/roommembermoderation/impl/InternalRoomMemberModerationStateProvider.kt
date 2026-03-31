/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.roommembermoderation.api.ModerationAction
import io.prism.android.features.roommembermoderation.api.ModerationActionState
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationPermissions
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.toImmutableList

class InternalRoomMemberModerationStateProvider : PreviewParameterProvider<InternalRoomMemberModerationState> {
    override val values: Sequence<InternalRoomMemberModerationState>
        get() = sequenceOf(
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                actions = listOf(
                    ModerationActionState(action = ModerationAction.DisplayProfile, isEnabled = true),
                ),
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                actions = listOf(
                    ModerationActionState(action = ModerationAction.DisplayProfile, isEnabled = true),
                    ModerationActionState(action = ModerationAction.KickUser, isEnabled = true),
                ),
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                actions = listOf(
                    ModerationActionState(action = ModerationAction.DisplayProfile, isEnabled = true),
                    ModerationActionState(action = ModerationAction.KickUser, isEnabled = false),
                    ModerationActionState(action = ModerationAction.BanUser, isEnabled = true),
                    ),
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                actions = listOf(
                    ModerationActionState(action = ModerationAction.DisplayProfile, isEnabled = true),
                    ModerationActionState(action = ModerationAction.KickUser, isEnabled = false),
                    ModerationActionState(action = ModerationAction.UnbanUser, isEnabled = true),
                ),
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                kickUserAsyncAction = AsyncAction.ConfirmingNoParams,
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                kickUserAsyncAction = AsyncAction.Loading,
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                banUserAsyncAction = AsyncAction.ConfirmingNoParams,
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                banUserAsyncAction = AsyncAction.Loading,
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                unbanUserAsyncAction = AsyncAction.ConfirmingNoParams,
            ),
            aRoomMembersModerationState(
                selectedUser = anAlice(),
                unbanUserAsyncAction = AsyncAction.Loading,
            ),
        )
}

fun anAlice() = PRISMUser(
    UserId(value = "@alice:server.org"),
    displayName = "Alice",
    avatarUrl = null,
)

fun aRoomMembersModerationState(
    permissions: RoomMemberModerationPermissions = RoomMemberModerationPermissions.DEFAULT,
    selectedUser: PRISMUser? = null,
    actions: List<ModerationActionState> = emptyList(),
    kickUserAsyncAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    banUserAsyncAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    unbanUserAsyncAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (RoomMemberModerationEvents) -> Unit = {},
) = InternalRoomMemberModerationState(
    permissions = permissions,
    selectedUser = selectedUser,
    actions = actions.toImmutableList(),
    kickUserAsyncAction = kickUserAsyncAction,
    banUserAsyncAction = banUserAsyncAction,
    unbanUserAsyncAction = unbanUserAsyncAction,
    eventSink = eventSink,
)
