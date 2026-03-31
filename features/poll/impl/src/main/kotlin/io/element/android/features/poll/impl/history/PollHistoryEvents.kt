/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.impl.history

import io.prism.android.features.poll.impl.history.model.PollHistoryFilter
import io.prism.android.libraries.prism.api.core.EventId

sealed interface PollHistoryEvents {
    data object LoadMore : PollHistoryEvents
    data class SelectPollAnswer(val pollStartId: EventId, val answerId: String) : PollHistoryEvents
    data class EndPoll(val pollStartId: EventId) : PollHistoryEvents
    data class SelectFilter(val filter: PollHistoryFilter) : PollHistoryEvents
}
