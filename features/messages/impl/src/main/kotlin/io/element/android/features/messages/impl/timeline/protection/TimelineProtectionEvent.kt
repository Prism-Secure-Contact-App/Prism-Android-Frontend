/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.protection

import io.prism.android.libraries.prism.api.core.EventId

sealed interface TimelineProtectionEvent {
    data class ShowContent(val eventId: EventId?) : TimelineProtectionEvent
}
