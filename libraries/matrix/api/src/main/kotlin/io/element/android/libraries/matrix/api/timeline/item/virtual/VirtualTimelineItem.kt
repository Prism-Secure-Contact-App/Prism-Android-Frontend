/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.timeline.item.virtual

import io.prism.android.libraries.prism.api.timeline.Timeline

sealed interface VirtualTimelineItem {
    data class DayDivider(
        val timestamp: Long
    ) : VirtualTimelineItem

    data object ReadMarker : VirtualTimelineItem

    data object RoomBeginning : VirtualTimelineItem

    data object LastForwardIndicator : VirtualTimelineItem

    data class LoadingIndicator(
        val direction: Timeline.PaginationDirection,
        val timestamp: Long,
    ) : VirtualTimelineItem

    data object TypingNotification : VirtualTimelineItem
}
