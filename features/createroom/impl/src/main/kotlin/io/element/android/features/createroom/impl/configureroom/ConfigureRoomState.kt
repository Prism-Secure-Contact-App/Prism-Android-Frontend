/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.impl.configureroom

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.prism.ui.media.AvatarAction
import io.prism.android.libraries.prism.ui.room.address.RoomAddressValidity
import io.prism.android.libraries.permissions.api.PermissionsState
import kotlinx.collections.immutable.ImmutableList

data class ConfigureRoomState(
    val isSpace: Boolean,
    val config: CreateRoomConfig,
    val avatarActions: ImmutableList<AvatarAction>,
    val createRoomAction: AsyncAction<RoomId>,
    val cameraPermissionState: PermissionsState,
    val roomAddressValidity: RoomAddressValidity,
    val homeserverName: String,
    val availableJoinRules: ImmutableList<JoinRuleItem>,
    val spaces: ImmutableList<SpaceRoom>,
    val eventSink: (ConfigureRoomEvents) -> Unit
) {
    val isValid: Boolean = config.roomName?.isNotEmpty() == true &&
        (config.visibilityState is RoomVisibilityState.Private || roomAddressValidity == RoomAddressValidity.Valid) &&
        config.visibilityState.joinRuleItem in availableJoinRules
}
