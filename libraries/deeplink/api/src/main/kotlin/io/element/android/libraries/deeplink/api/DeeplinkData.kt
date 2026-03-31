/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.deeplink.api

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.ThreadId

sealed interface DeeplinkData {
    /** Session id is common for all deep links. */
    val sessionId: SessionId

    /** The target is the root of the app, with the given [sessionId]. */
    data class Root(override val sessionId: SessionId) : DeeplinkData

    /** The target is a room, with the given [sessionId], [roomId] and optionally a [threadId] and [eventId]. */
    data class Room(override val sessionId: SessionId, val roomId: RoomId, val threadId: ThreadId?, val eventId: EventId?) : DeeplinkData
}
