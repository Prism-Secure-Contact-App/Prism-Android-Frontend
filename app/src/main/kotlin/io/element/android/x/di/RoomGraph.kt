/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.di

import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.prism.android.appnav.di.TimelineBindings
import io.prism.android.libraries.architecture.NodeFactoriesBindings
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.room.BaseRoom
import io.prism.android.libraries.prism.api.room.JoinedRoom

@GraphExtension(RoomScope::class)
interface RoomGraph : NodeFactoriesBindings, TimelineBindings {
    @GraphExtension.Factory
    interface Factory {
        fun create(
            @Provides joinedRoom: JoinedRoom,
            @Provides baseRoom: BaseRoom
        ): RoomGraph
    }
}
