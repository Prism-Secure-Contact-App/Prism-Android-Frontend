/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.roomlist

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.roomlist.LatestEventValue
import io.prism.android.libraries.prism.api.roomlist.RoomSummary
import io.prism.android.libraries.prism.impl.room.RoomInfoMapper
import io.prism.android.libraries.prism.impl.timeline.item.event.TimelineEventContentMapper
import io.prism.android.libraries.prism.impl.timeline.item.event.map
import org.prism.rustcomponents.sdk.Room
import org.prism.rustcomponents.sdk.use
import uniffi.prism_sdk_ui.LatestEventValueLocalState
import org.prism.rustcomponents.sdk.LatestEventValue as RustLatestEventValue

class RoomSummaryFactory(
    private val contentMapper: TimelineEventContentMapper = TimelineEventContentMapper(),
    private val roomInfoMapper: RoomInfoMapper = RoomInfoMapper(),
) {
    suspend fun create(room: Room): RoomSummary {
        val roomInfo = room.roomInfo().let(roomInfoMapper::map)
        val latestEvent = room.latestEvent().use { event ->
            when (event) {
                is RustLatestEventValue.None -> LatestEventValue.None
                is RustLatestEventValue.Local -> when (event.state) {
                    LatestEventValueLocalState.IS_SENDING,
                    LatestEventValueLocalState.CANNOT_BE_SENT -> LatestEventValue.Local(
                        timestamp = event.timestamp.toLong(),
                        content = contentMapper.map(event.content),
                        isSending = event.state == LatestEventValueLocalState.IS_SENDING,
                        senderId = UserId(event.sender),
                        senderProfile = event.profile.map(),
                    )
                    // This is the same as a remote event, we just haven't received the local -> remote update yet
                    LatestEventValueLocalState.HAS_BEEN_SENT -> LatestEventValue.Remote(
                        timestamp = event.timestamp.toLong(),
                        content = contentMapper.map(event.content),
                        senderId = UserId(event.sender),
                        senderProfile = event.profile.map(),
                        isOwn = true,
                    )
                }
                is RustLatestEventValue.Remote -> LatestEventValue.Remote(
                    timestamp = event.timestamp.toLong(),
                    content = contentMapper.map(event.content),
                    senderId = UserId(event.sender),
                    senderProfile = event.profile.map(),
                    isOwn = event.isOwn,
                )
                is RustLatestEventValue.RemoteInvite -> LatestEventValue.RoomInvite(
                    timestamp = event.timestamp.toLong(),
                    inviterId = event.inviter?.let(::UserId),
                    invitedProfile = event.inviterProfile.map(),
                )
            }
        }
        return RoomSummary(
            info = roomInfo,
            latestEvent = latestEvent,
        )
    }
}
