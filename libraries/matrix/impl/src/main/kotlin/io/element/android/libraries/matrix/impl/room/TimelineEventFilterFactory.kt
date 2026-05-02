/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.room

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.matrix.api.room.StateEventType
import org.matrix.rustcomponents.sdk.FilterTimelineEventType
import org.matrix.rustcomponents.sdk.TimelineEventFilter

interface TimelineEventFilterFactory {
    fun create(listStateEventType: List<StateEventType>): TimelineEventFilter
}

@ContributesBinding(AppScope::class)
class RustTimelineEventFilterFactory : TimelineEventFilterFactory {
    override fun create(listStateEventType: List<StateEventType>): TimelineEventFilter {
        return TimelineEventFilter.excludeEventTypes(
            listStateEventType.map { stateEventType ->
                FilterTimelineEventType.State(stateEventType.map())
            }
        )
    }
}
