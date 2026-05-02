/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.notifications.edit

import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList

data class EditNotificationSettingRoomInfo(
    val roomId: RoomId,
    val name: String?,
    val heroesAvatar: ImmutableList<AvatarData>,
    val avatarData: AvatarData,
    val notificationMode: RoomNotificationMode?
)
