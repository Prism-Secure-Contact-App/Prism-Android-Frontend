/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.features.startchat.api.StartDMAction
import io.prism.android.features.startchat.impl.userlist.SelectionMode
import io.prism.android.features.startchat.impl.userlist.UserListDataStore
import io.prism.android.features.startchat.impl.userlist.UserListPresenter
import io.prism.android.features.startchat.impl.userlist.UserListPresenterArgs
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.usersearch.api.UserRepository
import kotlinx.coroutines.launch

@Inject
class StartChatPresenter(
    presenterFactory: UserListPresenter.Factory,
    userRepository: UserRepository,
    userListDataStore: UserListDataStore,
    private val startDMAction: StartDMAction,
    private val buildMeta: BuildMeta,
    private val featureFlagService: FeatureFlagService,
) : Presenter<StartChatState> {
    private val presenter = presenterFactory.create(
        UserListPresenterArgs(
            selectionMode = SelectionMode.Single,
        ),
        userRepository,
        userListDataStore,
    )

    @Composable
    override fun present(): StartChatState {
        val userListState = presenter.present()

        val localCoroutineScope = rememberCoroutineScope()
        val startDmActionState: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val isRoomDirectorySearchEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.RoomDirectorySearch)
        }.collectAsState(initial = false)

        fun handleEvent(event: StartChatEvents) {
            when (event) {
                is StartChatEvents.StartDM -> localCoroutineScope.launch {
                    startDMAction.execute(
                        prismUser = event.prismUser,
                        createIfDmDoesNotExist = startDmActionState.value is AsyncAction.Confirming,
                        actionState = startDmActionState,
                    )
                }
                StartChatEvents.CancelStartDM -> startDmActionState.value = AsyncAction.Uninitialized
            }
        }

        return StartChatState(
            applicationName = buildMeta.applicationName,
            userListState = userListState,
            startDmAction = startDmActionState.value,
            isRoomDirectorySearchEnabled = isRoomDirectorySearchEnabled,
            eventSink = ::handleEvent,
        )
    }
}
