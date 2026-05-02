/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.blockedusers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.features.preferences.impl.R
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.components.async.AsyncIndicator
import io.prism.android.libraries.designsystem.components.async.AsyncIndicatorHost
import io.prism.android.libraries.designsystem.components.async.rememberAsyncIndicatorState
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Scaffold
import io.prism.android.libraries.designsystem.theme.components.TopAppBar
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.components.MatrixUserRow
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersView(
    state: BlockedUsersState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    titleStr = stringResource(CommonStrings.common_blocked_users),
                    navigationIcon = {
                        BackButton(onClick = onBackClick)
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(state.blockedUsers) { matrixUser ->
                    BlockedUserItem(
                        matrixUser = matrixUser,
                        onClick = { state.eventSink(BlockedUsersEvents.Unblock(it)) }
                    )
                }
            }
        }

        val asyncIndicatorState = rememberAsyncIndicatorState()
        AsyncIndicatorHost(modifier = Modifier.statusBarsPadding(), state = asyncIndicatorState)

        when (state.unblockUserAction) {
            is AsyncAction.Loading -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.enqueue {
                        AsyncIndicator.Loading(text = stringResource(R.string.screen_blocked_users_unblocking))
                    }
                }
            }
            is AsyncAction.Failure -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.enqueue(durationMs = AsyncIndicator.DURATION_SHORT) {
                        AsyncIndicator.Failure(text = stringResource(CommonStrings.common_failed))
                    }
                }
            }
            is AsyncAction.Success -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.clear()
                }
            }
            is AsyncAction.Confirming -> {
                ConfirmationDialog(
                    title = stringResource(R.string.screen_blocked_users_unblock_alert_title),
                    content = stringResource(R.string.screen_blocked_users_unblock_alert_description),
                    submitText = stringResource(R.string.screen_blocked_users_unblock_alert_action),
                    onSubmitClick = { state.eventSink(BlockedUsersEvents.ConfirmUnblock) },
                    onDismiss = { state.eventSink(BlockedUsersEvents.Cancel) }
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun BlockedUserItem(
    matrixUser: PRISMUser,
    onClick: (UserId) -> Unit,
) {
    MatrixUserRow(
        modifier = Modifier.clickable { onClick(matrixUser.userId) },
        matrixUser = matrixUser,
    )
}

@PreviewsDayNight
@Composable
internal fun BlockedUsersViewPreview(@PreviewParameter(BlockedUsersStateProvider::class) state: BlockedUsersState) {
    PRISMPreview {
        BlockedUsersView(
            state = state,
            onBackClick = {}
        )
    }
}
