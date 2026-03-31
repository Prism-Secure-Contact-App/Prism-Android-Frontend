/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemPollContent
import io.prism.android.libraries.designsystem.preview.PRISMPreviewLight

// Note: no need for light/dark variant for this preview, we only look at the timestamp position
@Preview
@Composable
internal fun TimelineItemEventTimestampBelowPreview() = PRISMPreviewLight {
    ATimelineItemEventRow(
        event = aTimelineItemEvent(content = aTimelineItemPollContent()),
    )
}
