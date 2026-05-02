/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.test

import io.prism.android.features.call.api.CallType
import io.prism.android.features.call.api.PRISMCallEntryPoint
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.tests.testutils.lambda.lambdaError

class FakePRISMCallEntryPoint(
    var startCallResult: (CallType) -> Unit = { lambdaError() },
    var handleIncomingCallResult: (
        CallType.RoomCall,
        EventId,
        UserId,
        String?,
        String?,
        String?,
        String,
        String?,
    ) -> Unit = { _, _, _, _, _, _, _, _ -> lambdaError() }
) : PRISMCallEntryPoint {
    override fun startCall(callType: CallType) {
        startCallResult(callType)
    }

    override suspend fun handleIncomingCall(
        callType: CallType.RoomCall,
        eventId: EventId,
        senderId: UserId,
        roomName: String?,
        senderName: String?,
        avatarUrl: String?,
        timestamp: Long,
        expirationTimestamp: Long,
        notificationChannelId: String,
        textContent: String?,
    ) {
        handleIncomingCallResult(
            callType,
            eventId,
            senderId,
            roomName,
            senderName,
            avatarUrl,
            notificationChannelId,
            textContent,
        )
    }
}
