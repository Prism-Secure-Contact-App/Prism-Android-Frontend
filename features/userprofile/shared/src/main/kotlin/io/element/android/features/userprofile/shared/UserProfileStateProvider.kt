/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.shared

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.startchat.api.ConfirmingStartDmWithPRISMUser
import io.prism.android.features.userprofile.api.UserProfileEvents
import io.prism.android.features.userprofile.api.UserProfileState
import io.prism.android.features.userprofile.api.UserProfileVerificationState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.ui.components.aMatrixUser

open class UserProfileStateProvider : PreviewParameterProvider<UserProfileState> {
    override val values: Sequence<UserProfileState>
        get() = sequenceOf(
            aUserProfileState(),
            aUserProfileState(userName = null),
            aUserProfileState(isBlocked = AsyncData.Success(true), verificationState = UserProfileVerificationState.VERIFIED),
            aUserProfileState(displayConfirmationDialog = UserProfileState.ConfirmationDialog.Block),
            aUserProfileState(displayConfirmationDialog = UserProfileState.ConfirmationDialog.Unblock),
            aUserProfileState(isBlocked = AsyncData.Loading(true), verificationState = UserProfileVerificationState.UNKNOWN),
            aUserProfileState(startDmActionState = AsyncAction.Loading),
            aUserProfileState(canCall = true),
            aUserProfileState(startDmActionState = ConfirmingStartDmWithPRISMUser(aMatrixUser())),
            aUserProfileState(verificationState = UserProfileVerificationState.VERIFICATION_VIOLATION),
        )
}

fun aUserProfileState(
    userId: UserId = UserId("@daniel:domain.com"),
    userName: String? = "Daniel",
    avatarUrl: String? = null,
    isBlocked: AsyncData<Boolean> = AsyncData.Success(false),
    verificationState: UserProfileVerificationState = UserProfileVerificationState.UNVERIFIED,
    startDmActionState: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    displayConfirmationDialog: UserProfileState.ConfirmationDialog? = null,
    isCurrentUser: Boolean = false,
    dmRoomId: RoomId? = null,
    canCall: Boolean = false,
    snackbarMessage: SnackbarMessage? = null,
    eventSink: (UserProfileEvents) -> Unit = {},
) = UserProfileState(
    userId = userId,
    userName = userName,
    avatarUrl = avatarUrl,
    isBlocked = isBlocked,
    verificationState = verificationState,
    startDmActionState = startDmActionState,
    displayConfirmationDialog = displayConfirmationDialog,
    isCurrentUser = isCurrentUser,
    dmRoomId = dmRoomId,
    canCall = canCall,
    snackbarMessage = snackbarMessage,
    eventSink = eventSink,
)
