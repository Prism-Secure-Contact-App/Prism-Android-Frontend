/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model.event

import io.prism.android.libraries.prism.api.timeline.item.event.EventType

data object TimelineItemLegacyCallInviteContent : TimelineItemEventContent {
    override val type: String
        get() = EventType.CALL_INVITE
}
