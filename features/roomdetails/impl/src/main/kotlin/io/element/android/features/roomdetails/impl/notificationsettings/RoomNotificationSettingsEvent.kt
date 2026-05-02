/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl.notificationsettings

import io.prism.android.libraries.matrix.api.room.RoomNotificationMode

sealed interface RoomNotificationSettingsEvent {
    data class ChangeRoomNotificationMode(val mode: RoomNotificationMode) : RoomNotificationSettingsEvent
    data class SetNotificationMode(val isDefault: Boolean) : RoomNotificationSettingsEvent
    data object DeleteCustomNotification : RoomNotificationSettingsEvent
    data object ClearSetNotificationError : RoomNotificationSettingsEvent
    data object ClearRestoreDefaultError : RoomNotificationSettingsEvent
}
