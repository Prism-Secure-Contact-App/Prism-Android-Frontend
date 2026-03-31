/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.impl.actions

import dev.zacsweers.metro.ContributesBinding
import uk.fathertkt.prism.features.analytics.plan.PollEnd
import io.prism.android.features.poll.api.actions.EndPollAction
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.timeline.Timeline
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesBinding(RoomScope::class)
class DefaultEndPollAction(
    private val analyticsService: AnalyticsService,
) : EndPollAction {
    override suspend fun execute(timeline: Timeline, pollStartId: EventId): Result<Unit> {
        return timeline.endPoll(
            pollStartId = pollStartId,
            text = "The poll with event id: $pollStartId has ended."
        ).onSuccess {
            analyticsService.capture(PollEnd())
        }
    }
}
