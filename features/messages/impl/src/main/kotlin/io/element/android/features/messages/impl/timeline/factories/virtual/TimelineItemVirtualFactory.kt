/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.factories.virtual

import dev.zacsweers.metro.Inject
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemLastForwardIndicatorModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemLoadingIndicatorModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemReadMarkerModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemRoomBeginningModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemTypingNotificationModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemVirtualModel
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.api.timeline.item.virtual.VirtualTimelineItem

@Inject
class TimelineItemVirtualFactory(
    private val daySeparatorFactory: TimelineItemDaySeparatorFactory,
) {
    fun create(
        virtualTimelineItem: PRISMTimelineItem.Virtual,
    ): TimelineItem.Virtual {
        return TimelineItem.Virtual(
            id = virtualTimelineItem.uniqueId,
            model = virtualTimelineItem.computeModel()
        )
    }

    private fun PRISMTimelineItem.Virtual.computeModel(): TimelineItemVirtualModel {
        return when (val inner = virtual) {
            is VirtualTimelineItem.DayDivider -> daySeparatorFactory.create(inner)
            is VirtualTimelineItem.ReadMarker -> TimelineItemReadMarkerModel
            is VirtualTimelineItem.RoomBeginning -> TimelineItemRoomBeginningModel
            is VirtualTimelineItem.LoadingIndicator -> TimelineItemLoadingIndicatorModel(
                direction = inner.direction,
                timestamp = inner.timestamp
            )
            is VirtualTimelineItem.LastForwardIndicator -> TimelineItemLastForwardIndicatorModel
            VirtualTimelineItem.TypingNotification -> TimelineItemTypingNotificationModel
        }
    }
}
