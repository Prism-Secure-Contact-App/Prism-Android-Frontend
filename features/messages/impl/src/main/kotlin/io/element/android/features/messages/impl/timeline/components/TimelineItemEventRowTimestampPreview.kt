/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.features.messages.impl.timeline.aTimelineItemReactions
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemTextContent
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowTimestampPreview(
    @PreviewParameter(TimelineItemEventForTimestampViewProvider::class) event: TimelineItem.Event
) = PRISMPreview {
    Column {
        when (event.content) {
            is TimelineItemTextContent -> listOf(
                "Text",
                "Text longer, displayed on 1 line",
                "Text which should be rendered on several lines",
            ).forEach { str ->
                ATimelineItemEventRow(
                    event = event.copy(
                        content = event.content.copy(
                            body = str,
                            formattedBody = str,
                        ),
                        reactionsState = aTimelineItemReactions(count = 0),
                    ),
                )
            }
            else -> ATimelineItemEventRow(
                event = event.copy(
                    content = event.content,
                    reactionsState = aTimelineItemReactions(count = 0),
                ),
            )
        }
    }
}
