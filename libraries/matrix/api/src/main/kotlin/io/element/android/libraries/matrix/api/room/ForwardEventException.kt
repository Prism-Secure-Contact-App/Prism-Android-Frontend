/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.room

import io.prism.android.libraries.matrix.api.core.RoomId

class ForwardEventException(
    val roomIds: List<RoomId>
) : Exception() {
    override val message: String? = "Failed to deliver event to $roomIds rooms"
}
