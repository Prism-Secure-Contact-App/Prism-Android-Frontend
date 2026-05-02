/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.impl.actions

import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.PollVote
import io.prism.android.features.poll.api.actions.SendPollResponseAction
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesBinding(RoomScope::class)
class DefaultSendPollResponseAction(
    private val analyticsService: AnalyticsService,
) : SendPollResponseAction {
    override suspend fun execute(timeline: Timeline, pollStartId: EventId, answerId: String): Result<Unit> {
        return timeline.sendPollResponse(
            pollStartId = pollStartId,
            answers = listOf(answerId),
        ).onSuccess {
            analyticsService.capture(PollVote())
        }
    }
}
