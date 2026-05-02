/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.timeline.postprocessor

import io.prism.android.libraries.matrix.api.core.UniqueId
import io.prism.android.libraries.matrix.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.MembershipChange
import io.prism.android.libraries.matrix.api.timeline.item.event.OtherState
import io.prism.android.libraries.matrix.api.timeline.item.event.StateContent
import io.prism.android.libraries.matrix.api.timeline.item.virtual.VirtualTimelineItem
import io.prism.android.libraries.matrix.test.A_USER_ID
import io.prism.android.libraries.matrix.test.A_USER_ID_2
import io.prism.android.libraries.matrix.test.timeline.aMessageContent
import io.prism.android.libraries.matrix.test.timeline.anEventTimelineItem
import io.prism.android.libraries.matrix.test.timeline.item.event.aRoomMembershipContent

internal val timelineStartEvent = PRISMTimelineItem.Virtual(
    uniqueId = UniqueId("timeline_start"),
    virtual = VirtualTimelineItem.RoomBeginning,
)
internal val roomCreateEvent = PRISMTimelineItem.Event(
    uniqueId = UniqueId("m.room.create"),
    event = anEventTimelineItem(sender = A_USER_ID, content = StateContent("", OtherState.RoomCreate))
)
internal val roomCreatorJoinEvent = PRISMTimelineItem.Event(
    uniqueId = UniqueId("m.room.member"),
    event = anEventTimelineItem(content = aRoomMembershipContent(userId = A_USER_ID, change = MembershipChange.JOINED))
)
internal val otherMemberJoinEvent = PRISMTimelineItem.Event(
    uniqueId = UniqueId("m.room.member_other"),
    event = anEventTimelineItem(content = aRoomMembershipContent(userId = A_USER_ID_2, change = MembershipChange.JOINED))
)
internal val messageEvent = PRISMTimelineItem.Event(
    uniqueId = UniqueId("m.room.message"),
    event = anEventTimelineItem(content = aMessageContent("hi"))
)
internal val messageEvent2 = PRISMTimelineItem.Event(
    uniqueId = UniqueId("m.room.message2"),
    event = anEventTimelineItem(content = aMessageContent("hello"))
)
internal val dayEvent = PRISMTimelineItem.Virtual(
    uniqueId = UniqueId("day"),
    virtual = VirtualTimelineItem.DayDivider(0),
)
