/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.api

import androidx.compose.runtime.MutableState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.user.PRISMUser

interface StartDMAction {
    /**
     * Try to find an existing DM with the given user, or create one if none exists.
     * @param matrixUser The user to start a DM with.
     * @param createIfDmDoesNotExist If true, create a DM if one does not exist. If false and the DM
     * does not exist, the action will fail with the value [ConfirmingStartDmWithPRISMUser].
     * @param actionState The state to update with the result of the action.
     */
    suspend fun execute(
        matrixUser: PRISMUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    )
}
