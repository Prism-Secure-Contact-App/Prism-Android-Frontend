/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl.notificationsettings

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.matrix.api.room.RoomNotificationMode
import io.prism.android.libraries.matrix.api.room.RoomNotificationSettings

data class RoomNotificationSettingsState(
    val showUserDefinedSettingStyle: Boolean,
    val roomName: String,
    val roomNotificationSettings: AsyncData<RoomNotificationSettings>,
    val pendingRoomNotificationMode: RoomNotificationMode?,
    val pendingSetDefault: Boolean?,
    val defaultRoomNotificationMode: RoomNotificationMode?,
    val setNotificationSettingAction: AsyncAction<Unit>,
    val restoreDefaultAction: AsyncAction<Unit>,
    val displayMentionsOnlyDisclaimer: Boolean,
    val eventSink: (RoomNotificationSettingsEvent) -> Unit
)

val RoomNotificationSettingsState.displayNotificationMode: RoomNotificationMode? get() {
    return pendingRoomNotificationMode ?: roomNotificationSettings.dataOrNull()?.mode
}

val RoomNotificationSettingsState.displayIsDefault: Boolean? get() {
    return pendingSetDefault ?: roomNotificationSettings.dataOrNull()?.isDefault
}
