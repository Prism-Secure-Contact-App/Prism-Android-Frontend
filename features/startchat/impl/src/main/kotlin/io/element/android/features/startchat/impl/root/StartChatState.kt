/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.root

import io.prism.android.features.startchat.impl.userlist.UserListState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.core.RoomId

data class StartChatState(
    val applicationName: String,
    val userListState: UserListState,
    val startDmAction: AsyncAction<RoomId>,
    val isRoomDirectorySearchEnabled: Boolean,
    val eventSink: (StartChatEvents) -> Unit,
)
