/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.roomlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.home.impl.R
import io.prism.android.features.home.impl.model.RoomListRoomSummary
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.prism.android.libraries.designsystem.theme.components.OutlinedButton
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.TextButton
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListDeclineInviteMenu(
    menu: RoomListState.DeclineInviteMenu.Shown,
    canReportRoom: Boolean,
    onDeclineAndBlockClick: (RoomListRoomSummary) -> Unit,
    eventSink: (RoomListEvent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { eventSink(RoomListEvent.HideDeclineInviteMenu) },
    ) {
        RoomListDeclineInviteMenuContent(
            roomName = menu.roomSummary.name ?: menu.roomSummary.roomId.value,
            onDeclineClick = {
                eventSink(RoomListEvent.HideDeclineInviteMenu)
                eventSink(RoomListEvent.DeclineInvite(menu.roomSummary, false))
            },
            onDeclineAndBlockClick = {
                eventSink(RoomListEvent.HideDeclineInviteMenu)
                if (canReportRoom) {
                    onDeclineAndBlockClick(menu.roomSummary)
                } else {
                    eventSink(RoomListEvent.DeclineInvite(menu.roomSummary, true))
                }
            },
            onCancelClick = {
                eventSink(RoomListEvent.HideDeclineInviteMenu)
            }
        )
    }
}

@Composable
private fun RoomListDeclineInviteMenuContent(
    roomName: String,
    onDeclineClick: () -> Unit,
    onDeclineAndBlockClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.screen_invites_decline_chat_title),
            style = PRISMTheme.typography.fontHeadingSmMedium,
            color = PRISMTheme.colors.textPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.screen_invites_decline_chat_message, roomName),
            style = PRISMTheme.typography.fontBodyMdRegular,
            color = PRISMTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(22.dp))
        Button(
            text = stringResource(CommonStrings.action_decline),
            modifier = Modifier.fillMaxWidth(),
            onClick = onDeclineClick,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            text = stringResource(CommonStrings.action_decline_and_block),
            modifier = Modifier.fillMaxWidth(),
            destructive = true,
            onClick = onDeclineAndBlockClick
        )
        Spacer(Modifier.height(16.dp))
        TextButton(
            text = stringResource(CommonStrings.action_cancel),
            modifier = Modifier.fillMaxWidth(),
            onClick = onCancelClick
        )
    }
}

// TODO This component should be seen in [RoomListView] @Preview but it doesn't show up.
// see: https://issuetracker.google.com/issues/283843380
// Remove this preview when the issue is fixed.
@PreviewsDayNight
@Composable
internal fun RoomListDeclineInviteMenuContentPreview() = PRISMPreview {
    RoomListDeclineInviteMenuContent(
        roomName = "Room name",
        onCancelClick = {},
        onDeclineClick = {},
        onDeclineAndBlockClick = {},
    )
}
