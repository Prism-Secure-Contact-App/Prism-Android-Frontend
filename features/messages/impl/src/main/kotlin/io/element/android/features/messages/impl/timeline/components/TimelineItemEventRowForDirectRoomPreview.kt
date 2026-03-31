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
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.features.messages.impl.timeline.aTimelineRoomInfo
import io.prism.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemImageContent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowForDirectRoomPreview() = PRISMPreview {
    Column {
        sequenceOf(false, true).forEach {
            ATimelineItemEventRow(
                event = aTimelineItemEvent(
                    isMine = it,
                    content = aTimelineItemTextContent(
                        body = "A long text which will be displayed on several lines and" +
                            " hopefully can be manually adjusted to test different behaviors."
                    ),
                    groupPosition = TimelineItemGroupPosition.First,
                ),
                timelineRoomInfo = aTimelineRoomInfo(
                    isDm = true,
                ),
            )
            ATimelineItemEventRow(
                event = aTimelineItemEvent(
                    isMine = it,
                    content = aTimelineItemImageContent(
                        aspectRatio = 5f
                    ),
                    groupPosition = TimelineItemGroupPosition.Last,
                ),
                timelineRoomInfo = aTimelineRoomInfo(
                    isDm = true,
                ),
            )
        }
    }
}
