/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.impl

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.call.api.CallType
import io.prism.android.features.call.api.PRISMCallEntryPoint
import io.prism.android.features.call.impl.notifications.CallNotificationData
import io.prism.android.features.call.impl.utils.ActiveCallManager
import io.prism.android.features.call.impl.utils.IntentProvider
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UserId

@ContributesBinding(AppScope::class)
class DefaultPRISMCallEntryPoint(
    @ApplicationContext private val context: Context,
    private val activeCallManager: ActiveCallManager,
) : PRISMCallEntryPoint {
    companion object {
        const val EXTRA_CALL_TYPE = "EXTRA_CALL_TYPE"
        const val REQUEST_CODE = 2255
    }

    override fun startCall(callType: CallType) {
        context.startActivity(IntentProvider.createIntent(context, callType))
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
        val incomingCallNotificationData = CallNotificationData(
            sessionId = callType.sessionId,
            roomId = callType.roomId,
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            senderName = senderName,
            avatarUrl = avatarUrl,
            timestamp = timestamp,
            expirationTimestamp = expirationTimestamp,
            notificationChannelId = notificationChannelId,
            textContent = textContent,
            audioOnly = callType.isAudioCall
        )
        activeCallManager.registerIncomingCall(notificationData = incomingCallNotificationData)
    }
}
