/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.di

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.appnav.di.RoomGraphFactory
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.room.JoinedRoom

@ContributesBinding(SessionScope::class)
class DefaultRoomGraphFactory(
    private val sessionGraph: SessionGraph,
) : RoomGraphFactory {
    override fun create(room: JoinedRoom): Any {
        return sessionGraph.roomGraphFactory
            .create(room, room)
    }
}
