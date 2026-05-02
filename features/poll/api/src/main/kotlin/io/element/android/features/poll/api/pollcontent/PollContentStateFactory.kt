/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.api.pollcontent

import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.timeline.item.event.EventTimelineItem
import io.prism.android.libraries.matrix.api.timeline.item.event.PollContent

interface PollContentStateFactory {
    suspend fun create(eventTimelineItem: EventTimelineItem, content: PollContent): PollContentState {
        return create(
            eventId = eventTimelineItem.eventId,
            isEditable = eventTimelineItem.isEditable,
            isOwn = eventTimelineItem.isOwn,
            content = content,
        )
    }
    suspend fun create(eventId: EventId?, isEditable: Boolean, isOwn: Boolean, content: PollContent): PollContentState
}
