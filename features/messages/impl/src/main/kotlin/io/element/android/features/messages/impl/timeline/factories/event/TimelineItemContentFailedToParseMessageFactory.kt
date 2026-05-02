/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.factories.event

import dev.zacsweers.metro.Inject
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.prism.android.libraries.matrix.api.timeline.item.event.FailedToParseMessageLikeContent

@Inject
class TimelineItemContentFailedToParseMessageFactory {
    fun create(@Suppress("UNUSED_PARAMETER") failedToParseMessageLike: FailedToParseMessageLikeContent): TimelineItemEventContent {
        return TimelineItemUnknownContent
    }
}
