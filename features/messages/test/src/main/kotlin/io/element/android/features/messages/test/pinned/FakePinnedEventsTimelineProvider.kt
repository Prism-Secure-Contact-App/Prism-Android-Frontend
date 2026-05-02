/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.test.pinned

import io.prism.android.features.messages.api.pinned.PinnedEventsTimelineProvider
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.libraries.matrix.test.timeline.FakeTimelineProvider
import kotlinx.coroutines.flow.StateFlow

class FakePinnedEventsTimelineProvider(
    private val fakeTimelineProvider: FakeTimelineProvider = FakeTimelineProvider(),
) : PinnedEventsTimelineProvider {
    override fun activeTimelineFlow(): StateFlow<Timeline?> = fakeTimelineProvider.activeTimelineFlow()
}
