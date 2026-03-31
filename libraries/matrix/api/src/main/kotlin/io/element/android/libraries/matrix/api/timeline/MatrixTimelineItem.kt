/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.timeline

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.TransactionId
import io.prism.android.libraries.prism.api.core.UniqueId
import io.prism.android.libraries.prism.api.timeline.item.event.EventTimelineItem
import io.prism.android.libraries.prism.api.timeline.item.virtual.VirtualTimelineItem

sealed interface PRISMTimelineItem {
    data class Event(val uniqueId: UniqueId, val event: EventTimelineItem) : PRISMTimelineItem {
        val eventId: EventId? = event.eventId
        val transactionId: TransactionId? = event.transactionId
    }

    data class Virtual(val uniqueId: UniqueId, val virtual: VirtualTimelineItem) : PRISMTimelineItem
    data object Other : PRISMTimelineItem
}
