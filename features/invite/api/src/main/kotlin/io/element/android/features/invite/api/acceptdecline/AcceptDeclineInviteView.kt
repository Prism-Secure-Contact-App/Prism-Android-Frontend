/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.api.acceptdecline

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.prism.android.libraries.prism.api.core.RoomId

fun interface AcceptDeclineInviteView {
    @Composable
    fun Render(
        state: AcceptDeclineInviteState,
        onAcceptInviteSuccess: (RoomId) -> Unit,
        onDeclineInviteSuccess: (RoomId) -> Unit,
        modifier: Modifier,
    )
}
