/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.factories.virtual

import dev.zacsweers.metro.Inject
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemDaySeparatorModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemVirtualModel
import io.prism.android.libraries.dateformatter.api.DateFormatter
import io.prism.android.libraries.dateformatter.api.DateFormatterMode
import io.prism.android.libraries.prism.api.timeline.item.virtual.VirtualTimelineItem

@Inject
class TimelineItemDaySeparatorFactory(
    private val dateFormatter: DateFormatter,
) {
    fun create(virtualItem: VirtualTimelineItem.DayDivider): TimelineItemVirtualModel {
        val formattedDate = dateFormatter.format(
            timestamp = virtualItem.timestamp,
            mode = DateFormatterMode.Day,
            useRelative = true,
        )
        return TimelineItemDaySeparatorModel(
            formattedDate = formattedDate
        )
    }
}
