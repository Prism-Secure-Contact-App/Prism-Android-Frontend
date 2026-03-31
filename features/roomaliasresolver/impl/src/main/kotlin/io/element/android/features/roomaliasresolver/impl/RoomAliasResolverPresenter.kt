/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.architecture.runCatchingUpdatingState
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.room.alias.ResolvedRoomAlias
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrElse

@AssistedInject
class RoomAliasResolverPresenter(
    @Assisted private val roomAlias: RoomAlias,
    private val prismClient: PRISMClient,
) : Presenter<RoomAliasResolverState> {
    fun interface Factory {
        fun create(
            roomAlias: RoomAlias,
        ): RoomAliasResolverPresenter
    }

    @Composable
    override fun present(): RoomAliasResolverState {
        val coroutineScope = rememberCoroutineScope()
        val resolveState: MutableState<AsyncData<ResolvedRoomAlias>> = remember { mutableStateOf(AsyncData.Uninitialized) }
        LaunchedEffect(Unit) {
            resolveAlias(resolveState)
        }

        fun handleEvent(event: RoomAliasResolverEvents) {
            when (event) {
                RoomAliasResolverEvents.Retry -> coroutineScope.resolveAlias(resolveState)
                RoomAliasResolverEvents.DismissError -> resolveState.value = AsyncData.Uninitialized
            }
        }

        return RoomAliasResolverState(
            roomAlias = roomAlias,
            resolveState = resolveState.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.resolveAlias(resolveState: MutableState<AsyncData<ResolvedRoomAlias>>) = launch {
        suspend {
            prismClient.resolveRoomAlias(roomAlias)
                .getOrThrow()
                .getOrElse { throw RoomAliasResolverFailures.UnknownAlias }
        }.runCatchingUpdatingState(resolveState)
    }
}
