/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.pinned.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.messages.impl.UserEventPermissions
import io.prism.android.features.messages.impl.actionlist.ActionListState
import io.prism.android.features.messages.impl.actionlist.anActionListState
import io.prism.android.features.messages.impl.link.LinkState
import io.prism.android.features.messages.impl.link.aLinkState
import io.prism.android.features.messages.impl.timeline.TimelineRoomInfo
import io.prism.android.features.messages.impl.timeline.aTimelineItemDaySeparator
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.features.messages.impl.timeline.aTimelineItemReactions
import io.prism.android.features.messages.impl.timeline.aTimelineRoomInfo
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemAudioContent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemFileContent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemPollContent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.prism.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.prism.android.features.messages.impl.timeline.protection.aTimelineProtectionState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

open class PinnedMessagesListStateProvider : PreviewParameterProvider<PinnedMessagesListState> {
    override val values: Sequence<PinnedMessagesListState>
        get() = sequenceOf(
            aFailedPinnedMessagesListState(),
            aLoadingPinnedMessagesListState(),
            anEmptyPinnedMessagesListState(),
            aLoadedPinnedMessagesListState(
                timelineItems = persistentListOf(
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemTextContent("A pinned message"),
                        groupPosition = TimelineItemGroupPosition.Last,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemAudioContent("A pinned file"),
                        groupPosition = TimelineItemGroupPosition.Middle,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = false,
                        content = aTimelineItemPollContent("A pinned poll?"),
                        groupPosition = TimelineItemGroupPosition.First,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemDaySeparator(),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemTextContent("A pinned message"),
                        groupPosition = TimelineItemGroupPosition.Last,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemFileContent("A pinned file?"),
                        groupPosition = TimelineItemGroupPosition.Middle,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                    aTimelineItemEvent(
                        isMine = true,
                        content = aTimelineItemPollContent("A pinned poll?"),
                        groupPosition = TimelineItemGroupPosition.First,
                        timelineItemReactions = aTimelineItemReactions(0)
                    ),
                )
            )
        )
}

fun aFailedPinnedMessagesListState() = PinnedMessagesListState.Failed

fun aLoadingPinnedMessagesListState() = PinnedMessagesListState.Loading

fun anEmptyPinnedMessagesListState() = PinnedMessagesListState.Empty

fun aLoadedPinnedMessagesListState(
    timelineRoomInfo: TimelineRoomInfo = aTimelineRoomInfo(),
    timelineProtectionState: TimelineProtectionState = aTimelineProtectionState(),
    linkState: LinkState = aLinkState(),
    timelineItems: List<TimelineItem> = emptyList(),
    actionListState: ActionListState = anActionListState(),
    aUserEventPermissions: UserEventPermissions = UserEventPermissions.DEFAULT,
    displayThreadSummaries: Boolean = false,
    eventSink: (PinnedMessagesListEvent) -> Unit = {}
) = PinnedMessagesListState.Filled(
    timelineRoomInfo = timelineRoomInfo,
    timelineProtectionState = timelineProtectionState,
    linkState = linkState,
    timelineItems = timelineItems.toImmutableList(),
    actionListState = actionListState,
    userEventPermissions = aUserEventPermissions,
    displayThreadSummaries = displayThreadSummaries,
    eventSink = eventSink,
)
