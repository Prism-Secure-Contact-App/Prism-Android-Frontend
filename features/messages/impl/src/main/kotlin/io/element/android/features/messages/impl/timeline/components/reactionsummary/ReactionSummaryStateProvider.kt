/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.reactionsummary

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.messages.impl.timeline.aTimelineItemReactions
import io.prism.android.libraries.prism.api.core.EventId

open class ReactionSummaryStateProvider : PreviewParameterProvider<ReactionSummaryState> {
    override val values = sequenceOf(aReactionSummaryState())
}

fun aReactionSummaryState(): ReactionSummaryState {
    val reactions = aTimelineItemReactions(8, true).reactions
    return ReactionSummaryState(
        target = ReactionSummaryState.Summary(
            reactions = reactions,
            selectedKey = reactions[0].key,
            selectedEventId = EventId("$1234"),
        ),
        eventSink = {}
    )
}
