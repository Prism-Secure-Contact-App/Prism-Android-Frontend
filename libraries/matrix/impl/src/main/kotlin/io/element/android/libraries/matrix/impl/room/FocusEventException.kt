/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.room.errors.FocusEventException
import org.prism.rustcomponents.sdk.FocusEventException as RustFocusEventException

fun Throwable.toFocusEventException(): Throwable {
    return when (this) {
        is RustFocusEventException -> {
            when (this) {
                is RustFocusEventException.InvalidEventId -> {
                    FocusEventException.InvalidEventId(eventId, err)
                }
                is RustFocusEventException.EventNotFound -> {
                    FocusEventException.EventNotFound(EventId(eventId))
                }
                is RustFocusEventException.Other -> {
                    FocusEventException.Other(msg)
                }
            }
        }
        else -> {
            this
        }
    }
}
