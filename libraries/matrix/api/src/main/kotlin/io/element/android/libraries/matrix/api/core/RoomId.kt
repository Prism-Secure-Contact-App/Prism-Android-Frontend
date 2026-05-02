/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.core

import io.prism.android.libraries.androidutils.metadata.isInDebug
import java.io.Serializable

@JvmInline
value class RoomId(val value: String) : Serializable {
    init {
        if (isInDebug && !PRISMPatterns.isRoomId(value)) {
            error("`$value` is not a valid room id.\n Example room id: `!room_id:domain`.")
        }
    }

    override fun toString(): String = value
}
