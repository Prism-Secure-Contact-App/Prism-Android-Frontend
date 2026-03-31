/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.di

import io.prism.android.features.messages.api.pinned.PinnedEventsTimelineProvider
import io.prism.android.libraries.prism.api.timeline.TimelineProvider
import io.prism.android.services.analytics.api.watchers.AnalyticsSendMessageWatcher

interface TimelineBindings {
    val timelineProvider: TimelineProvider
    val pinnedEventsTimelineProvider: PinnedEventsTimelineProvider
    val analyticsSendMessageWatcher: AnalyticsSendMessageWatcher
}
