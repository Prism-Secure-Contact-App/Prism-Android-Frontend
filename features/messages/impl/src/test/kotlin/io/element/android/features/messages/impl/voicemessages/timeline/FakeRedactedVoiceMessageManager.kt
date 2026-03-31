/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.voicemessages.timeline

import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem

class FakeRedactedVoiceMessageManager : RedactedVoiceMessageManager {
    private val _invocations: MutableList<List<PRISMTimelineItem>> = mutableListOf()
    val invocations: List<List<PRISMTimelineItem>>
        get() = _invocations

    override suspend fun onEachPRISMTimelineItem(timelineItems: List<PRISMTimelineItem>) {
        _invocations.add(timelineItems)
    }
}
