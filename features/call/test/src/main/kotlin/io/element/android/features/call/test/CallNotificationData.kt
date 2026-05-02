/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.test

import io.prism.android.features.call.impl.notifications.CallNotificationData
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.test.AN_AVATAR_URL
import io.prism.android.libraries.matrix.test.AN_EVENT_ID
import io.prism.android.libraries.matrix.test.A_ROOM_ID
import io.prism.android.libraries.matrix.test.A_ROOM_NAME
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.A_USER_ID_2
import io.prism.android.libraries.matrix.test.A_USER_NAME

fun aCallNotificationData(
    sessionId: SessionId = A_SESSION_ID,
    roomId: RoomId = A_ROOM_ID,
    eventId: EventId = AN_EVENT_ID,
    senderId: UserId = A_USER_ID_2,
    roomName: String = A_ROOM_NAME,
    senderName: String? = A_USER_NAME,
    avatarUrl: String? = AN_AVATAR_URL,
    notificationChannelId: String = "channel_id",
    timestamp: Long = 0L,
    expirationTimestamp: Long = 30_000L,
    textContent: String? = null,
    audioOnly: Boolean = false,
): CallNotificationData = CallNotificationData(
    sessionId = sessionId,
    roomId = roomId,
    eventId = eventId,
    senderId = senderId,
    roomName = roomName,
    senderName = senderName,
    avatarUrl = avatarUrl,
    notificationChannelId = notificationChannelId,
    timestamp = timestamp,
    expirationTimestamp = expirationTimestamp,
    textContent = textContent,
    audioOnly = audioOnly
)
