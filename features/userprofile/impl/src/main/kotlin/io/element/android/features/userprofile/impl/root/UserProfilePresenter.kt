/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.features.enterprise.api.SessionEnterpriseService
import io.prism.android.features.startchat.api.StartDMAction
import io.prism.android.features.userprofile.api.UserProfileEvents
import io.prism.android.features.userprofile.api.UserProfileState
import io.prism.android.features.userprofile.api.UserProfileState.ConfirmationDialog
import io.prism.android.features.userprofile.api.UserProfileVerificationState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.room.powerlevels.canCall
import io.prism.android.libraries.matrix.api.room.powerlevels.use
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
class UserProfilePresenter(
    @Assisted private val userId: UserId,
    private val client: PRISMClient,
    private val startDMAction: StartDMAction,
    private val sessionEnterpriseService: SessionEnterpriseService,
) : Presenter<UserProfileState> {
    @AssistedFactory
    interface Factory {
        fun create(userId: UserId): UserProfilePresenter
    }

    @Composable
    private fun getDmRoomId(): State<RoomId?> {
        return produceState(initialValue = null) {
            value = client.findDM(userId).getOrNull()
        }
    }

    @Composable
    private fun getCanCall(roomId: RoomId?): State<Boolean> {
        val isPRISMCallAvailable by produceState(initialValue = false, roomId) {
            value = sessionEnterpriseService.isPRISMCallAvailable()
        }
        return produceState(initialValue = false, isPRISMCallAvailable, roomId) {
            value = when {
                isPRISMCallAvailable.not() -> false
                client.isMe(userId) -> false
                else ->
                    roomId
                        ?.let { client.getRoom(it) }
                        ?.use { room ->
                            room.roomPermissions().use(false) { perms -> perms.canCall() }
                        }
                        .orFalse()
            }
        }
    }

    @Composable
    override fun present(): UserProfileState {
        val coroutineScope = rememberCoroutineScope()
        val isCurrentUser = remember { client.isMe(userId) }
        var confirmationDialog by remember { mutableStateOf<ConfirmationDialog?>(null) }
        val startDmActionState: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val isBlocked: MutableState<AsyncData<Boolean>> = remember { mutableStateOf(AsyncData.Uninitialized) }
        val dmRoomId by getDmRoomId()
        val canCall by getCanCall(dmRoomId)
        LaunchedEffect(Unit) {
            client.ignoredUsersFlow
                .map { ignoredUsers -> userId in ignoredUsers }
                .distinctUntilChanged()
                .onEach { isBlocked.value = AsyncData.Success(it) }
                .launchIn(this)
        }
        val userProfile by produceState<PRISMUser?>(null) { value = client.getProfile(userId).getOrNull() }

        fun handleEvent(event: UserProfileEvents) {
            when (event) {
                is UserProfileEvents.BlockUser -> {
                    if (event.needsConfirmation) {
                        confirmationDialog = ConfirmationDialog.Block
                    } else {
                        confirmationDialog = null
                        coroutineScope.blockUser(isBlocked)
                    }
                }
                is UserProfileEvents.UnblockUser -> {
                    if (event.needsConfirmation) {
                        confirmationDialog = ConfirmationDialog.Unblock
                    } else {
                        confirmationDialog = null
                        coroutineScope.unblockUser(isBlocked)
                    }
                }
                UserProfileEvents.ClearConfirmationDialog -> confirmationDialog = null
                UserProfileEvents.ClearBlockUserError -> {
                    isBlocked.value = AsyncData.Success(isBlocked.value.dataOrNull().orFalse())
                }
                UserProfileEvents.StartDM -> {
                    coroutineScope.launch {
                        startDMAction.execute(
                            matrixUser = userProfile ?: PRISMUser(userId),
                            createIfDmDoesNotExist = startDmActionState.value is AsyncAction.Confirming,
                            actionState = startDmActionState,
                        )
                    }
                }
                UserProfileEvents.ClearStartDMState -> {
                    startDmActionState.value = AsyncAction.Uninitialized
                }
                // Do nothing for other event as they are handled by the RoomMemberDetailsPresenter if needed
                UserProfileEvents.WithdrawVerification,
                is UserProfileEvents.CopyToClipboard -> Unit
            }
        }

        return UserProfileState(
            userId = userId,
            userName = userProfile?.displayName,
            avatarUrl = userProfile?.avatarUrl,
            isBlocked = isBlocked.value,
            verificationState = UserProfileVerificationState.UNKNOWN,
            startDmActionState = startDmActionState.value,
            displayConfirmationDialog = confirmationDialog,
            isCurrentUser = isCurrentUser,
            dmRoomId = dmRoomId,
            canCall = canCall,
            snackbarMessage = null,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.blockUser(
        isBlockedState: MutableState<AsyncData<Boolean>>,
    ) = launch {
        isBlockedState.value = AsyncData.Loading(false)
        client.ignoreUser(userId)
            .onFailure {
                isBlockedState.value = AsyncData.Failure(it, false)
            }
        // Note: on success, ignoredUsersFlow will emit new item.
    }

    private fun CoroutineScope.unblockUser(
        isBlockedState: MutableState<AsyncData<Boolean>>,
    ) = launch {
        isBlockedState.value = AsyncData.Loading(true)
        client.unignoreUser(userId)
            .onFailure {
                isBlockedState.value = AsyncData.Failure(it, true)
            }
        // Note: on success, ignoredUsersFlow will emit new item.
    }
}
