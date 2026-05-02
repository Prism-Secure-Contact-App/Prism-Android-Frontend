/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.room.join

import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.JoinedRoom
import io.prism.android.libraries.core.extensions.mapFailure
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.RoomIdOrAlias
import io.prism.android.libraries.matrix.api.exception.ClientException
import io.prism.android.libraries.matrix.api.exception.ErrorKind
import io.prism.android.libraries.matrix.api.room.join.JoinRoom
import io.prism.android.libraries.matrix.impl.analytics.toAnalyticsJoinedRoom
import io.prism.android.services.analytics.api.AnalyticsService

@ContributesBinding(SessionScope::class)
class DefaultJoinRoom(
    private val client: PRISMClient,
    private val analyticsService: AnalyticsService,
) : JoinRoom {
    override suspend fun invoke(
        roomIdOrAlias: RoomIdOrAlias,
        serverNames: List<String>,
        trigger: JoinedRoom.Trigger
    ): Result<Unit> {
        return when (roomIdOrAlias) {
            is RoomIdOrAlias.Id -> {
                if (serverNames.isEmpty()) {
                    client.joinRoom(roomIdOrAlias.roomId)
                } else {
                    client.joinRoomByIdOrAlias(roomIdOrAlias, serverNames)
                }
            }
            is RoomIdOrAlias.Alias -> {
                client.joinRoomByIdOrAlias(roomIdOrAlias, serverNames = emptyList())
            }
        }.onSuccess { roomInfo ->
            if (roomInfo != null) {
                analyticsService.capture(roomInfo.toAnalyticsJoinedRoom(trigger))
            }
        }.mapFailure {
            if (it is ClientException.PRISMApi) {
                when (it.kind) {
                    ErrorKind.Forbidden -> JoinRoom.Failures.UnauthorizedJoin
                    else -> it
                }
            } else {
                it
            }
        }.map { }
    }
}
