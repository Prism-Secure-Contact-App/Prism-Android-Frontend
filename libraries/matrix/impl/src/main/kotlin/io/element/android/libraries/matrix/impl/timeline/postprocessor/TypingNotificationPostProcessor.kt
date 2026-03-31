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

/**
 * This post processor is responsible for adding a typing notification item to the timeline items when the timeline is in live mode.
 */
class TypingNotificationPostProcessor(private val mode: Timeline.Mode) {
    fun process(items: List<PRISMTimelineItem>): List<PRISMTimelineItem> {
        return if (mode is Timeline.Mode.Live) {
            buildList {
                addAll(items)
                add(
                    PRISMTimelineItem.Virtual(
                        uniqueId = UniqueId("TypingNotification"),
                        virtual = VirtualTimelineItem.TypingNotification
                    )
                )
            }
        } else {
            items
        }
    }
}
