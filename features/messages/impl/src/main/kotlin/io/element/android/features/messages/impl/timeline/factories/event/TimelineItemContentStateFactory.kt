/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.factories.event

import dev.zacsweers.metro.Inject
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import io.prism.android.libraries.core.extensions.orEmpty
import io.prism.android.libraries.eventformatter.api.TimelineEventFormatter
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.timeline.item.event.EventContent

@Inject
class TimelineItemContentStateFactory(
    private val timelineEventFormatter: TimelineEventFormatter,
) {
    fun create(eventContent: EventContent, isOutgoing: Boolean, sender: UserId, senderDisambiguatedDisplayName: String): TimelineItemEventContent {
        val text = timelineEventFormatter.format(eventContent, isOutgoing, sender, senderDisambiguatedDisplayName)
        return TimelineItemStateEventContent(text.orEmpty().toString())
    }
}
