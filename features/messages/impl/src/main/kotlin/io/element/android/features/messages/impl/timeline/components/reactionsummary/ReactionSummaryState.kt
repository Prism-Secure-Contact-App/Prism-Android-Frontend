/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.reactionsummary

import io.prism.android.features.messages.impl.timeline.model.AggregatedReaction
import io.prism.android.libraries.matrix.api.core.EventId
import kotlinx.collections.immutable.ImmutableList

data class ReactionSummaryState(
    val target: Summary?,
    val eventSink: (ReactionSummaryEvent) -> Unit
) {
    data class Summary(
        val reactions: ImmutableList<AggregatedReaction>,
        val selectedKey: String,
        val selectedEventId: EventId
    )
}
