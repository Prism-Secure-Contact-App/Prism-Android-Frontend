/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.api

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId

data class UserProfileState(
    val userId: UserId,
    val userName: String?,
    val avatarUrl: String?,
    val verificationState: UserProfileVerificationState,
    val isBlocked: AsyncData<Boolean>,
    val startDmActionState: AsyncAction<RoomId>,
    val displayConfirmationDialog: ConfirmationDialog?,
    val isCurrentUser: Boolean,
    val dmRoomId: RoomId?,
    val canCall: Boolean,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (UserProfileEvents) -> Unit
) {
    enum class ConfirmationDialog {
        Block,
        Unblock
    }
}

enum class UserProfileVerificationState {
    UNKNOWN,
    VERIFIED,
    UNVERIFIED,
    VERIFICATION_VIOLATION,
}
