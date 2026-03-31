/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.pinned.list

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.timeline.item.TimelineItemDebugInfo

class FakePinnedMessagesListNavigator : PinnedMessagesListNavigator {
    var onViewInTimelineClickLambda: ((EventId) -> Unit)? = null
    override fun viewInTimeline(eventId: EventId) {
        onViewInTimelineClickLambda?.invoke(eventId)
    }

    var onShowEventDebugInfoClickLambda: ((EventId?, TimelineItemDebugInfo) -> Unit)? = null
    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
        onShowEventDebugInfoClickLambda?.invoke(eventId, debugInfo)
    }

    var onForwardEventClickLambda: ((EventId) -> Unit)? = null
    override fun forwardEvent(eventId: EventId) {
        onForwardEventClickLambda?.invoke(eventId)
    }
}
