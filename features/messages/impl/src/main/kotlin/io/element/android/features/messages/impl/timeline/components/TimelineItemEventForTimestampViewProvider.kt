/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemRedactedContent
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.prism.android.libraries.prism.api.timeline.item.event.LocalEventSendState
import io.prism.android.libraries.prism.api.timeline.item.event.MessageShield

class TimelineItemEventForTimestampViewProvider : PreviewParameterProvider<TimelineItem.Event> {
    override val values: Sequence<TimelineItem.Event>
        get() = sequenceOf(
            aTimelineItemEvent(),
            aTimelineItemEvent().copy(localSendState = LocalEventSendState.Sending.Event),
            aTimelineItemEvent().copy(localSendState = LocalEventSendState.Failed.Unknown("AN_ERROR")),
            // Edited
            aTimelineItemEvent().copy(content = aTimelineItemTextContent().copy(isEdited = true)),
            // Sending failed + Edited (not sure this is possible IRL, but should be covered by test)
            aTimelineItemEvent().copy(
                localSendState = LocalEventSendState.Failed.Unknown("AN_ERROR"),
                content = aTimelineItemTextContent().copy(isEdited = true),
            ),
            aTimelineItemEvent(
                messageShield = MessageShield.AuthenticityNotGuaranteed(isCritical = false),
            ),
            aTimelineItemEvent(
                messageShield = MessageShield.UnknownDevice(isCritical = true),
            ),
            aTimelineItemEvent(
                content = aTimelineItemRedactedContent(),
                messageShield = MessageShield.SentInClear(isCritical = true),
            ),
        )
}
