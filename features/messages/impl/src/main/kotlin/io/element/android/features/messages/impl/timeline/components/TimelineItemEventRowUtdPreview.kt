/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.features.messages.impl.timeline.aTimelineItemReactions
import io.prism.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.prism.api.timeline.item.event.UnableToDecryptContent
import io.prism.android.libraries.prism.api.timeline.item.event.UtdCause

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowUtdPreview() = PRISMPreview {
    Column {
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = "Alice",
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.UnsignedDevice,
                    )
                ),
                timelineItemReactions = aTimelineItemReactions(count = 0),
                groupPosition = TimelineItemGroupPosition.First,
            ),
        )
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = "Bob",
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.VerificationViolation,
                    )
                ),
                groupPosition = TimelineItemGroupPosition.First,
                timelineItemReactions = aTimelineItemReactions(count = 0)
            ),
        )

        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = "Bob",
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.SentBeforeWeJoined,
                    )
                ),
                groupPosition = TimelineItemGroupPosition.Last,
                timelineItemReactions = aTimelineItemReactions(count = 0)
            ),
        )
    }
}
