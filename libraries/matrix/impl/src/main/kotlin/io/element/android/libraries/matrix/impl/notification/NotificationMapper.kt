/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.notification

import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.ThreadId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.notification.NotificationContent
import io.prism.android.libraries.prism.api.notification.NotificationData
import io.prism.android.libraries.prism.api.room.isDm
import io.prism.android.libraries.prism.impl.room.join.map
import io.prism.android.services.toolbox.api.systemclock.SystemClock
import org.prism.rustcomponents.sdk.NotificationEvent
import org.prism.rustcomponents.sdk.NotificationItem
import org.prism.rustcomponents.sdk.use

class NotificationMapper(
    private val clock: SystemClock,
) {
    private val notificationContentMapper = NotificationContentMapper()

    fun map(
        sessionId: SessionId,
        eventId: EventId,
        roomId: RoomId,
        notificationItem: NotificationItem
    ): Result<NotificationData> {
        return runCatchingExceptions {
            notificationItem.use { item ->
                val isDm = isDm(
                    isDirect = item.roomInfo.isDirect,
                    activeMembersCount = item.roomInfo.joinedMembersCount.toInt(),
                )
                val timestamp = item.timestamp() ?: clock.epochMillis()
                NotificationData(
                    sessionId = sessionId,
                    eventId = eventId,
                    threadId = item.threadId?.let(::ThreadId),
                    roomId = roomId,
                    senderAvatarUrl = item.senderInfo.avatarUrl,
                    senderDisplayName = item.senderInfo.displayName,
                    senderIsNameAmbiguous = item.senderInfo.isNameAmbiguous,
                    roomAvatarUrl = item.roomInfo.avatarUrl ?: item.senderInfo.avatarUrl.takeIf { isDm },
                    roomDisplayName = item.roomInfo.displayName,
                    isDirect = item.roomInfo.isDirect,
                    isDm = isDm,
                    isSpace = item.roomInfo.isSpace,
                    isEncrypted = item.roomInfo.isEncrypted.orFalse(),
                    isNoisy = item.isNoisy.orFalse(),
                    timestamp = timestamp,
                    content = notificationContentMapper.map(item.event).getOrThrow(),
                    hasMention = item.hasMention.orFalse(),
                    roomJoinRule = item.roomInfo.joinRule?.map(),
                )
            }
        }
    }
}

class NotificationContentMapper {
    private val timelineEventToNotificationContentMapper = TimelineEventToNotificationContentMapper()

    fun map(notificationEvent: NotificationEvent): Result<NotificationContent> =
        when (notificationEvent) {
            is NotificationEvent.Timeline -> timelineEventToNotificationContentMapper.map(notificationEvent.event)
            is NotificationEvent.Invite -> Result.success(
                NotificationContent.Invite(
                    senderId = UserId(notificationEvent.sender),
                )
            )
        }
}

private fun NotificationItem.timestamp(): Long? {
    return (this.event as? NotificationEvent.Timeline)?.event?.timestamp()?.toLong()
}
