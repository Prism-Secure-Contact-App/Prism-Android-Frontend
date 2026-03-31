/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.eventformatter.api

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.timeline.item.event.EventContent
import io.prism.android.libraries.prism.api.timeline.item.event.EventTimelineItem
import io.prism.android.libraries.prism.api.timeline.item.event.getDisambiguatedDisplayName

interface TimelineEventFormatter {
    fun format(event: EventTimelineItem): CharSequence? {
        return format(
            content = event.content,
            isOutgoing = event.isOwn,
            sender = event.sender,
            senderDisambiguatedDisplayName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
        )
    }
    fun format(content: EventContent, isOutgoing: Boolean, sender: UserId, senderDisambiguatedDisplayName: String): CharSequence?
}
