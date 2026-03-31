/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.verifysession.impl.outgoing

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.verifysession.impl.outgoing.OutgoingVerificationState.Step
import io.prism.android.features.verifysession.impl.ui.aDecimalsSessionVerificationData
import io.prism.android.features.verifysession.impl.ui.aEmojisSessionVerificationData
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.verification.VerificationRequest

open class OutgoingVerificationStateProvider : PreviewParameterProvider<OutgoingVerificationState> {
    override val values: Sequence<OutgoingVerificationState>
        get() = sequenceOf(
            anOutgoingVerificationState(
                step = Step.Initial,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Initial,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.AwaitingOtherDeviceResponse,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.AwaitingOtherDeviceResponse,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Uninitialized),
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Uninitialized),
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aEmojisSessionVerificationData(), AsyncData.Loading())
            ),
            anOutgoingVerificationState(
                step = Step.Canceled
            ),
            anOutgoingVerificationState(
                step = Step.Ready
            ),
            anOutgoingVerificationState(
                step = Step.Verifying(aDecimalsSessionVerificationData(), AsyncData.Uninitialized)
            ),
            anOutgoingVerificationState(
                step = Step.Completed,
                request = anOutgoingSessionVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Completed,
                request = anOutgoingUserVerificationRequest(),
            ),
            anOutgoingVerificationState(
                step = Step.Loading
            ),
            anOutgoingVerificationState(
                step = Step.Exit
            ),
            // Add other state here
        )
}

internal fun anOutgoingUserVerificationRequest() = VerificationRequest.Outgoing.User(userId = UserId("@alice:example.com"))
internal fun anOutgoingSessionVerificationRequest() = VerificationRequest.Outgoing.CurrentSession

internal fun anOutgoingVerificationState(
    step: Step = Step.Initial,
    request: VerificationRequest.Outgoing = anOutgoingSessionVerificationRequest(),
    eventSink: (OutgoingVerificationViewEvents) -> Unit = {},
) = OutgoingVerificationState(
    step = step,
    request = request,
    eventSink = eventSink,
)
