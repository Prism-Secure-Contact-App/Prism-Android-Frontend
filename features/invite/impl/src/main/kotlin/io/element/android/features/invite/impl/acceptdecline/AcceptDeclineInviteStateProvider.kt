/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl.acceptdecline

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.invite.api.InviteData
import io.prism.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.prism.android.features.invite.api.acceptdecline.ConfirmingDeclineInvite
import io.prism.android.features.invite.api.acceptdecline.anAcceptDeclineInviteState
import io.prism.android.features.invite.impl.AcceptInvite
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.core.RoomId

open class AcceptDeclineInviteStateProvider : PreviewParameterProvider<AcceptDeclineInviteState> {
    override val values: Sequence<AcceptDeclineInviteState>
        get() = sequenceOf(
            anAcceptDeclineInviteState(),
            anAcceptDeclineInviteState(
                declineAction = ConfirmingDeclineInvite(
                    InviteData(
                        roomId = RoomId("!room:prism.org"),
                        isDm = true,
                        roomName = "Alice"
                    ),
                    blockUser = false,
                ),
            ),
            anAcceptDeclineInviteState(
                declineAction = ConfirmingDeclineInvite(
                    InviteData(
                        roomId = RoomId("!room:prism.org"),
                        isDm = true,
                        roomName = "Alice"
                    ),
                    blockUser = true,
                ),
            ),
            anAcceptDeclineInviteState(
                acceptAction = AsyncAction.Failure(RuntimeException("Error while accepting invite")),
            ),
            anAcceptDeclineInviteState(
                acceptAction = AsyncAction.Failure(AcceptInvite.Failures.InvalidInvite),
            ),
            anAcceptDeclineInviteState(
                declineAction = AsyncAction.Failure(RuntimeException("Error while declining invite")),
            ),
        )
}
