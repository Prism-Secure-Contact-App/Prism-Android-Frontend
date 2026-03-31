/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl.di

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomIdOrAlias

interface KnockRoom {
    suspend operator fun invoke(
        roomIdOrAlias: RoomIdOrAlias,
        message: String,
        serverNames: List<String>,
    ): Result<Unit>
}

@ContributesBinding(SessionScope::class)
class DefaultKnockRoom(private val client: PRISMClient) : KnockRoom {
    override suspend fun invoke(
        roomIdOrAlias: RoomIdOrAlias,
        message: String,
        serverNames: List<String>
    ): Result<Unit> {
        return client
            .knockRoom(roomIdOrAlias, message, serverNames)
            .map { }
    }
}
