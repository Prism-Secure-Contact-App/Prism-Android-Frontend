/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.room

import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.ThreadId

sealed interface CreateTimelineParams {
    data class Focused(val focusedEventId: EventId) : CreateTimelineParams
    data object MediaOnly : CreateTimelineParams
    data class MediaOnlyFocused(val focusedEventId: EventId) : CreateTimelineParams
    data object PinnedOnly : CreateTimelineParams
    data class Threaded(val threadRootEventId: ThreadId) : CreateTimelineParams
}
