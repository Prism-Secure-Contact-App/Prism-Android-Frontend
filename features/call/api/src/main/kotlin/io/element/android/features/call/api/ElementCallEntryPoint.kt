/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.api

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UserId

/**
 * Entry point for the call feature.
 */
interface PRISMCallEntryPoint {
    /**
     * Start a call of the given type.
     * @param callType The type of call to start.
     */
    fun startCall(callType: CallType)

    /**
     * Handle an incoming call.
     * @param callType The type of call.
     * @param eventId The event id of the event that started the call.
     * @param senderId The user id of the sender of the event that started the call.
     * @param roomName The name of the room the call is in.
     * @param senderName The name of the sender of the event that started the call.
     * @param avatarUrl The avatar url of the room or DM.
     * @param timestamp The timestamp of the event that started the call.
     * @param expirationTimestamp The timestamp at which the call should stop ringing.
     * @param notificationChannelId The id of the notification channel to use for the call notification.
     * @param textContent The text content of the notification. If null the default content from the system will be used.
     */
    suspend fun handleIncomingCall(
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
    )
}
