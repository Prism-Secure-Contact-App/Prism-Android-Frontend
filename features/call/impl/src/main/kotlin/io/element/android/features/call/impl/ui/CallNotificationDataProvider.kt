/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.impl.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.call.impl.notifications.CallNotificationData
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.UserId

open class CallNotificationDataProvider : PreviewParameterProvider<CallNotificationData> {
    override val values: Sequence<CallNotificationData>
        get() = sequenceOf(
            aCallNotificationData(
                audioOnly = false
            ),
            aCallNotificationData(
                audioOnly = true
            ),
        )
}

internal fun aCallNotificationData(
    audioOnly: Boolean
): CallNotificationData {
    return CallNotificationData(
        sessionId = SessionId("@alice:prism.org"),
        roomId = RoomId("!1234:prism.org"),
        eventId = EventId("\$asdadadsad:prism.org"),
        senderId = UserId("@bob:prism.org"),
        roomName = "A room",
        senderName = "Bob",
        avatarUrl = null,
        notificationChannelId = "incoming_call",
        timestamp = 0L,
        textContent = null,
        expirationTimestamp = 1000L,
        audioOnly = audioOnly
    )
}
