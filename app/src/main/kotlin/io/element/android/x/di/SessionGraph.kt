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
import io.prism.android.libraries.architecture.NodeFactoriesBindings
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient

@GraphExtension(SessionScope::class)
interface SessionGraph : NodeFactoriesBindings {
    val roomGraphFactory: RoomGraph.Factory

    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides prismClient: PRISMClient): SessionGraph
    }
}
