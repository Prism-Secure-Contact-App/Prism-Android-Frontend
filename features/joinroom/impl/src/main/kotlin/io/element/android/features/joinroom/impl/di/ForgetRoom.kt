/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl.di

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId

interface ForgetRoom {
    suspend operator fun invoke(roomId: RoomId): Result<Unit>
}

@ContributesBinding(SessionScope::class)
class DefaultForgetRoom(private val client: PRISMClient) : ForgetRoom {
    override suspend fun invoke(roomId: RoomId): Result<Unit> {
        return client.getRoom(roomId)?.use { it.forget() }
            ?: Result.failure(IllegalStateException("Room not found"))
    }
}
