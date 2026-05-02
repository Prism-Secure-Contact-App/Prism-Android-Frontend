/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.prism.android.features.roomaliasresolver.impl.RoomAliasResolverPresenter
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomAlias

@BindingContainer
@ContributesTo(SessionScope::class)
object RoomAliasResolverModule {
    @Provides
    fun providesJoinRoomPresenterFactory(
        client: PRISMClient,
    ): RoomAliasResolverPresenter.Factory {
        return object : RoomAliasResolverPresenter.Factory {
            override fun create(roomAlias: RoomAlias): RoomAliasResolverPresenter {
                return RoomAliasResolverPresenter(
                    roomAlias = roomAlias,
                    matrixClient = client,
                )
            }
        }
    }
}
