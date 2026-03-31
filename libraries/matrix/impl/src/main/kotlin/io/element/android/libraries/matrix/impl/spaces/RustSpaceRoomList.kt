/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.spaces

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.spaces.SpaceRoom
import io.prism.android.libraries.prism.api.spaces.SpaceRoomList
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import uniffi.prism_sdk_ui.SpaceRoomListPaginationState
import java.util.Optional
import org.prism.rustcomponents.sdk.SpaceRoomList as InnerSpaceRoomList

class RustSpaceRoomList(
    override val spaceId: RoomId,
    private val innerProvider: suspend () -> InnerSpaceRoomList,
    private val coroutineScope: CoroutineScope,
    spaceRoomMapper: SpaceRoomMapper,
    private val analyticsService: AnalyticsService,
) : SpaceRoomList {
    private val innerCompletable = CompletableDeferred<InnerSpaceRoomList>()

    override val currentSpaceFlow = MutableStateFlow<Optional<SpaceRoom>>(Optional.empty())

    override val spaceRoomsFlow = MutableSharedFlow<List<SpaceRoom>>(replay = 1, extraBufferCapacity = Int.MAX_VALUE)

    override val paginationStatusFlow: MutableStateFlow<SpaceRoomList.PaginationStatus> =
        MutableStateFlow(SpaceRoomList.PaginationStatus.Idle(hasMoreToLoad = false))
    private val spaceListUpdateProcessor = SpaceListUpdateProcessor(
        spaceRoomsFlow = spaceRoomsFlow,
        mapper = spaceRoomMapper,
        analyticsService = analyticsService,
    )

    init {
        coroutineScope.launch {
            val inner = innerProvider()
            innerCompletable.complete(inner)

            inner.paginationStateFlow()
                .onEach { paginationStatus ->
                    paginationStatusFlow.emit(paginationStatus.into())
                }
                .launchIn(this)

            inner.spaceListUpdateFlow()
                .onEach { updates ->
                    spaceListUpdateProcessor.postUpdates(updates)
                }
                .launchIn(this)

            inner.spaceUpdateFlow()
                .map { space -> space.map(spaceRoomMapper::map) }
                .onEach { space ->
                    currentSpaceFlow.emit(space)
                }
                .launchIn(this)
        }
    }

    override suspend fun paginate(): Result<Unit> {
        return runCatchingExceptions {
            innerCompletable.await().paginate()
        }
    }

    override suspend fun reset(): Result<Unit> {
        return runCatchingExceptions {
            innerCompletable.await().reset()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun destroy() {
        Timber.d("Destroying SpaceRoomList $spaceId")
        coroutineScope.cancel()
        try {
            innerCompletable.getCompleted().destroy()
        } catch (_: Exception) {
            // Ignore, we just want to make sure it's completed
        }
    }

    private fun SpaceRoomListPaginationState.into(): SpaceRoomList.PaginationStatus {
        return when (this) {
            is SpaceRoomListPaginationState.Idle -> SpaceRoomList.PaginationStatus.Idle(hasMoreToLoad = !endReached)
            SpaceRoomListPaginationState.Loading -> SpaceRoomList.PaginationStatus.Loading
        }
    }
}
