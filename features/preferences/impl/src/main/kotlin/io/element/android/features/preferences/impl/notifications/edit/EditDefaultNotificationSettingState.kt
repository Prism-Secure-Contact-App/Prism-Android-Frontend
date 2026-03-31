/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.notifications.edit

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList

data class EditDefaultNotificationSettingState(
    val isOneToOne: Boolean,
    val mode: RoomNotificationMode?,
    val roomsWithUserDefinedMode: ImmutableList<EditNotificationSettingRoomInfo>,
    val changeNotificationSettingAction: AsyncAction<Unit>,
    val displayMentionsOnlyDisclaimer: Boolean,
    val eventSink: (EditDefaultNotificationSettingStateEvents) -> Unit,
)
