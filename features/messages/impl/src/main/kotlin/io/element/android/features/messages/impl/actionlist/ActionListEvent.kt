/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.actionlist

import io.prism.android.features.messages.impl.UserEventPermissions
import io.prism.android.features.messages.impl.timeline.model.TimelineItem

sealed interface ActionListEvent {
    data object Clear : ActionListEvent
    data class ComputeForMessage(
        val event: TimelineItem.Event,
        val userEventPermissions: UserEventPermissions,
    ) : ActionListEvent
}
