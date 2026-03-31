/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.impl.configureroom

import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.prism.ui.media.AvatarAction

sealed interface ConfigureRoomEvents {
    data class RoomNameChanged(val name: String) : ConfigureRoomEvents
    data class TopicChanged(val topic: String) : ConfigureRoomEvents
    data class JoinRuleChanged(val joinRuleItem: JoinRuleItem) : ConfigureRoomEvents
    data class RoomAddressChanged(val roomAddress: String) : ConfigureRoomEvents
    data object CreateRoom : ConfigureRoomEvents
    data class HandleAvatarAction(val action: AvatarAction) : ConfigureRoomEvents
    data class SetParentSpace(val space: SpaceRoom?) : ConfigureRoomEvents
    data object CancelCreateRoom : ConfigureRoomEvents
}
