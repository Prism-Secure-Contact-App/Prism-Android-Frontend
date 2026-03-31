/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.customreaction

import io.prism.android.features.messages.impl.timeline.model.TimelineItem

sealed interface CustomReactionEvent {
    data class ShowCustomReactionSheet(val event: TimelineItem.Event) : CustomReactionEvent
    data object DismissCustomReactionSheet : CustomReactionEvent
}
