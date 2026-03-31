/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import io.prism.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.libraries.prism.api.timeline.item.event.EventOrTransactionId
import io.prism.android.libraries.prism.api.user.PRISMUser

sealed interface MessagesEvent {
    data class HandleAction(val action: TimelineItemAction, val event: TimelineItem.Event) : MessagesEvent
    data class ToggleReaction(val emoji: String, val eventOrTransactionId: EventOrTransactionId) : MessagesEvent
    data class InviteDialogDismissed(val action: InviteDialogAction) : MessagesEvent
    data class OnUserClicked(val user: PRISMUser) : MessagesEvent
    data object MarkAsFullyReadAndExit : MessagesEvent
}

enum class InviteDialogAction {
    Cancel,
    Invite,
}
