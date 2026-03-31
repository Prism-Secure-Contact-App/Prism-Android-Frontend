/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.pinned.banner

import androidx.compose.ui.text.AnnotatedString
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.eventformatter.api.PinnedMessagesBannerFormatter
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import kotlinx.coroutines.withContext

@Inject
class PinnedMessagesBannerItemFactory(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val formatter: PinnedMessagesBannerFormatter,
) {
    suspend fun create(timelineItem: PRISMTimelineItem): PinnedMessagesBannerItem? = withContext(coroutineDispatchers.computation) {
        when (timelineItem) {
            is PRISMTimelineItem.Event -> {
                val eventId = timelineItem.eventId ?: return@withContext null
                val formatted = formatter.format(timelineItem.event)
                PinnedMessagesBannerItem(
                    eventId = eventId,
                    formatted = if (formatted is AnnotatedString) {
                        formatted
                    } else {
                        AnnotatedString(formatted.toString())
                    },
                )
            }
            else -> null
        }
    }
}
