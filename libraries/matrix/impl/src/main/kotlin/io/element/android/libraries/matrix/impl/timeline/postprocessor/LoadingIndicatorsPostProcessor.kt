/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.timeline.postprocessor

import io.prism.android.libraries.prism.api.core.UniqueId
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.api.timeline.Timeline
import io.prism.android.libraries.prism.api.timeline.item.virtual.VirtualTimelineItem
import io.prism.android.services.toolbox.api.systemclock.SystemClock

class LoadingIndicatorsPostProcessor(private val systemClock: SystemClock) {
    fun process(
        items: List<PRISMTimelineItem>,
        hasMoreToLoadBackward: Boolean,
        hasMoreToLoadForward: Boolean,
    ): List<PRISMTimelineItem> {
        val shouldAddForwardLoadingIndicator = hasMoreToLoadForward && items.isNotEmpty()
        val currentTimestamp = systemClock.epochMillis()
        return buildList {
            if (hasMoreToLoadBackward) {
                val backwardLoadingIndicator = PRISMTimelineItem.Virtual(
                    uniqueId = UniqueId("BackwardLoadingIndicator"),
                    virtual = VirtualTimelineItem.LoadingIndicator(
                        direction = Timeline.PaginationDirection.BACKWARDS,
                        timestamp = currentTimestamp
                    )
                )
                add(backwardLoadingIndicator)
            }
            addAll(items)
            if (shouldAddForwardLoadingIndicator) {
                val forwardLoadingIndicator = PRISMTimelineItem.Virtual(
                    uniqueId = UniqueId("ForwardLoadingIndicator"),
                    virtual = VirtualTimelineItem.LoadingIndicator(
                        direction = Timeline.PaginationDirection.FORWARDS,
                        timestamp = currentTimestamp
                    )
                )
                add(forwardLoadingIndicator)
            }
        }
    }
}
