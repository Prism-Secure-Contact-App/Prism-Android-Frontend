/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.space.impl.leave

import io.prism.android.libraries.matrix.api.spaces.SpaceRoom

data class SelectableSpaceRoom(
    val spaceRoom: SpaceRoom,
    val isLastOwner: Boolean,
    val joinedMembersCount: Int,
    val isSelected: Boolean,
)
