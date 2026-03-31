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
import uk.fathertkt.prism.features.analytics.plan.CreatedRoom
import io.prism.android.features.startchat.api.ConfirmingStartDmWithPRISMUser
import io.prism.android.features.startchat.api.StartDMAction
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.StartDMResult
import io.prism.android.libraries.prism.api.room.startDM
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesBinding(SessionScope::class)
class DefaultStartDMAction(
    private val prismClient: PRISMClient,
    private val analyticsService: AnalyticsService,
) : StartDMAction {
    override suspend fun execute(
        prismUser: PRISMUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    ) {
        actionState.value = AsyncAction.Loading
        when (val result = prismClient.startDM(prismUser.userId, createIfDmDoesNotExist)) {
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
                actionState.value = ConfirmingStartDmWithPRISMUser(prismUser = prismUser)
            }
        }
    }
}
