/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.impl.configureroom

import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CreateRoomConfig(
    val roomName: String? = null,
    val topic: String? = null,
    val avatarUri: String? = null,
    val invites: ImmutableList<PRISMUser> = persistentListOf(),
    val visibilityState: RoomVisibilityState = RoomVisibilityState.Private(JoinRuleItem.PrivateVisibility.Private),
    val parentSpace: SpaceRoom? = null,
)
