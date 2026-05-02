/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.verifysession.impl.incoming

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.verifysession.impl.incoming.IncomingVerificationState.Step
import io.prism.android.features.verifysession.impl.ui.aDecimalsSessionVerificationData
import io.prism.android.features.verifysession.impl.ui.aEmojisSessionVerificationData
import io.prism.android.libraries.matrix.api.core.DeviceId
import io.prism.android.libraries.matrix.api.core.FlowId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.api.verification.SessionVerificationRequestDetails
import io.prism.android.libraries.matrix.api.verification.VerificationRequest

open class IncomingVerificationStateProvider : PreviewParameterProvider<IncomingVerificationState> {
    override val values: Sequence<IncomingVerificationState>
        get() = sequenceOf(
            anIncomingVerificationState(),
            anIncomingVerificationState(step = aStepInitial(isWaiting = false), verificationRequest = anIncomingSessionVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = false), verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = true), verificationRequest = anIncomingSessionVerificationRequest()),
            anIncomingVerificationState(step = aStepInitial(isWaiting = true), verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = false)),
            anIncomingVerificationState(
                step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = false),
                verificationRequest = anIncomingUserVerificationRequest()
            ),
            anIncomingVerificationState(step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = true)),
            anIncomingVerificationState(
                step = Step.Verifying(data = aEmojisSessionVerificationData(), isWaiting = true),
                verificationRequest = anIncomingUserVerificationRequest()
            ),
            anIncomingVerificationState(step = Step.Verifying(data = aDecimalsSessionVerificationData(), isWaiting = false)),
            anIncomingVerificationState(step = Step.Completed),
            anIncomingVerificationState(step = Step.Completed, verificationRequest = anIncomingUserVerificationRequest()),
            anIncomingVerificationState(step = Step.Failure),
            anIncomingVerificationState(step = Step.Canceled),
            // Add other state here
        )
}

internal fun aStepInitial(
    isWaiting: Boolean = false,
) = Step.Initial(
    deviceDisplayName = "PRISM X Android",
    deviceId = DeviceId("ILAKNDNASDLK"),
    formattedSignInTime = "12:34",
    isWaiting = isWaiting,
)

internal fun anIncomingSessionVerificationRequest() = VerificationRequest.Incoming.OtherSession(
    details = SessionVerificationRequestDetails(
        senderProfile = PRISMUser(
            userId = UserId("@alice:example.com"),
            displayName = "Alice",
            avatarUrl = null,
        ),
        flowId = FlowId("1234"),
        deviceId = DeviceId("ILAKNDNASDLK"),
        deviceDisplayName = "a device name",
        firstSeenTimestamp = 0,
    )
)

internal fun anIncomingUserVerificationRequest() = VerificationRequest.Incoming.User(
    details = SessionVerificationRequestDetails(
        senderProfile = PRISMUser(
            userId = UserId("@alice:example.com"),
            displayName = "Alice",
            avatarUrl = null,
        ),
        flowId = FlowId("1234"),
        deviceId = DeviceId("ILAKNDNASDLK"),
        deviceDisplayName = "a device name",
        firstSeenTimestamp = 0,
    )
)

internal fun anIncomingVerificationState(
    step: Step = aStepInitial(),
    verificationRequest: VerificationRequest.Incoming = anIncomingSessionVerificationRequest(),
    eventSink: (IncomingVerificationViewEvents) -> Unit = {},
) = IncomingVerificationState(
    step = step,
    request = verificationRequest,
    eventSink = eventSink,
)
