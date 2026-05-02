/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.test

import androidx.compose.runtime.MutableState
import io.prism.android.features.startchat.api.StartDMAction
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeStartDMAction(
    private val executeResult: (PRISMUser, Boolean, MutableState<AsyncAction<RoomId>>) -> Unit = { _, _, _ ->
        lambdaError()
    }
) : StartDMAction {
    override suspend fun execute(
        matrixUser: PRISMUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    ) {
        executeResult(matrixUser, createIfDmDoesNotExist, actionState)
    }
}
