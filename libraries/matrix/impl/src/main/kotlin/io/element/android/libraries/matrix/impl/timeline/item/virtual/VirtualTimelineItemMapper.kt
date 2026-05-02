/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.timeline.item.virtual

import io.prism.android.libraries.matrix.api.timeline.item.virtual.VirtualTimelineItem
import org.matrix.rustcomponents.sdk.VirtualTimelineItem as RustVirtualTimelineItem

class VirtualTimelineItemMapper {
    fun map(virtualTimelineItem: RustVirtualTimelineItem): VirtualTimelineItem {
        return when (virtualTimelineItem) {
            is RustVirtualTimelineItem.DateDivider -> VirtualTimelineItem.DayDivider(virtualTimelineItem.ts.toLong())
            RustVirtualTimelineItem.ReadMarker -> VirtualTimelineItem.ReadMarker
            RustVirtualTimelineItem.TimelineStart -> VirtualTimelineItem.RoomBeginning
        }
    }
}
