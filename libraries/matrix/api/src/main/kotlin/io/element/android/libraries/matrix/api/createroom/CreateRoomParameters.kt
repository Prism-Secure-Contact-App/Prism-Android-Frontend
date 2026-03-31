/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.createroom

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.history.RoomHistoryVisibility
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.roomdirectory.RoomVisibility
import java.util.Optional

data class CreateRoomParameters(
    val name: String?,
    val topic: String? = null,
    val isEncrypted: Boolean,
    val isDirect: Boolean = false,
    val visibility: RoomVisibility,
    val preset: RoomPreset,
    val invite: List<UserId>? = null,
    val avatar: String? = null,
    val joinRuleOverride: JoinRule? = null,
    val historyVisibilityOverride: RoomHistoryVisibility? = null,
    val roomAliasName: Optional<String> = Optional.empty(),
    val isSpace: Boolean = false,
)
