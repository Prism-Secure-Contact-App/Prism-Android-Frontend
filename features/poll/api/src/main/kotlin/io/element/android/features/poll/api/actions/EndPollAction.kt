/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.api.actions

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.timeline.Timeline

interface EndPollAction {
    suspend fun execute(timeline: Timeline, pollStartId: EventId): Result<Unit>
}
