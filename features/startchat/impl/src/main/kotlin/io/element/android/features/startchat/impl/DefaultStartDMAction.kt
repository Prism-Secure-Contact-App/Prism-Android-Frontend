/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl

import androidx.compose.runtime.MutableState
import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.CreatedRoom
import io.prism.android.features.startchat.api.ConfirmingStartDmWithPRISMUser
import io.prism.android.features.startchat.api.StartDMAction
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.StartDMResult
import io.prism.android.libraries.matrix.api.room.startDM
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesBinding(SessionScope::class)
class DefaultStartDMAction(
    private val matrixClient: PRISMClient,
    private val analyticsService: AnalyticsService,
) : StartDMAction {
    override suspend fun execute(
        matrixUser: PRISMUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    ) {
        actionState.value = AsyncAction.Loading
        when (val result = matrixClient.startDM(matrixUser.userId, createIfDmDoesNotExist)) {
            is StartDMResult.Success -> {
                if (result.isNew) {
                    analyticsService.capture(CreatedRoom(isDM = true))
                }
                actionState.value = AsyncAction.Success(result.roomId)
            }
            is StartDMResult.Failure -> {
                actionState.value = AsyncAction.Failure(result.throwable)
            }
            StartDMResult.DmDoesNotExist -> {
                actionState.value = ConfirmingStartDmWithPRISMUser(matrixUser = matrixUser)
            }
        }
    }
}
