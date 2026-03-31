/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rolesandpermissions.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import uk.fathertkt.prism.features.analytics.plan.RoomModeration
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.architecture.runUpdatingState
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.prism.api.room.JoinedRoom
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.api.room.powerlevels.UserRoleChange
import io.prism.android.libraries.prism.api.room.powerlevels.userCountWithRole
import io.prism.android.libraries.prism.ui.model.roleOf
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class RolesAndPermissionsPresenter(
    private val room: JoinedRoom,
    private val dispatchers: CoroutineDispatchers,
    private val analyticsService: AnalyticsService,
) : Presenter<RolesAndPermissionsState> {
    @Composable
    override fun present(): RolesAndPermissionsState {
        val coroutineScope = rememberCoroutineScope()
        val roomInfo by room.roomInfoFlow.collectAsState()
        val moderatorCount by remember {
            room.userCountWithRole { role -> role is RoomMember.Role.Moderator }
        }.collectAsState(null)

        val adminCount by remember {
            room.userCountWithRole { role -> role is RoomMember.Role.Admin || role is RoomMember.Role.Owner }
        }.collectAsState(null)

        val availableDemoteActions by remember {
            derivedStateOf {
                val currentRole = roomInfo.roleOf(room.sessionId)
                when (currentRole) {
                    is RoomMember.Role.Admin -> persistentListOf(SelfDemoteAction.ToModerator, SelfDemoteAction.ToMember)
                    is RoomMember.Role.Moderator -> persistentListOf(SelfDemoteAction.ToMember)
                    else -> persistentListOf()
                }
            }
        }
        val changeOwnRoleAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }
        val resetPermissionsAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        fun handleEvent(event: RolesAndPermissionsEvents) {
            when (event) {
                is RolesAndPermissionsEvents.ChangeOwnRole -> {
                    changeOwnRoleAction.value = AsyncAction.ConfirmingNoParams
                }
                is RolesAndPermissionsEvents.CancelPendingAction -> {
                    changeOwnRoleAction.value = AsyncAction.Uninitialized
                    resetPermissionsAction.value = AsyncAction.Uninitialized
                }
                is RolesAndPermissionsEvents.DemoteSelfTo -> coroutineScope.demoteSelfTo(
                    role = event.role,
                    changeOwnRoleAction = changeOwnRoleAction,
                )
                is RolesAndPermissionsEvents.ResetPermissions -> if (resetPermissionsAction.value.isConfirming()) {
                    coroutineScope.resetPermissions(resetPermissionsAction)
                } else {
                    resetPermissionsAction.value = AsyncAction.ConfirmingNoParams
                }
            }
        }

        return RolesAndPermissionsState(
            roomSupportsOwnerRole = roomInfo.privilegedCreatorRole,
            adminCount = adminCount,
            moderatorCount = moderatorCount,
            availableSelfDemoteActions = availableDemoteActions,
            changeOwnRoleAction = changeOwnRoleAction.value,
            resetPermissionsAction = resetPermissionsAction.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.demoteSelfTo(
        role: RoomMember.Role,
        changeOwnRoleAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        runUpdatingState(changeOwnRoleAction) {
            room.updateUsersRoles(listOf(UserRoleChange(room.sessionId, role)))
        }
    }

    private fun CoroutineScope.resetPermissions(
        resetPermissionsAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        runUpdatingState(resetPermissionsAction) {
            analyticsService.capture(RoomModeration(RoomModeration.Action.ResetPermissions))
            room.resetPowerLevels()
        }
    }
}
