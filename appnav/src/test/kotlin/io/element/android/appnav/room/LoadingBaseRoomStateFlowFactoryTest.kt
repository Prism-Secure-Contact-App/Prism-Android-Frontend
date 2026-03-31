/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.room

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.api.roomlist.RoomList
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.FakeJoinedRoom
import io.prism.android.libraries.prism.test.roomlist.FakeDynamicRoomList
import io.prism.android.libraries.prism.test.roomlist.FakeRoomListService
import io.prism.android.libraries.prism.ui.room.LoadingRoomState
import io.prism.android.libraries.prism.ui.room.LoadingRoomStateFlowFactory
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LoadingBaseRoomStateFlowFactoryTest {
    @Test
    fun `flow should emit only Loaded when we already pass a JoinedRoom`() = runTest {
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(sessionId = A_SESSION_ID, roomId = A_ROOM_ID))
        val prismClient = FakePRISMClient(A_SESSION_ID)
        val flowFactory = LoadingRoomStateFlowFactory(prismClient)
        flowFactory
            .create(lifecycleScope = this, roomId = A_ROOM_ID, joinedRoom = room)
            .test {
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loaded(room))
                ensureAllEventsConsumed()
            }
    }

    @Test
    fun `flow should emit Loading and then Loaded when there is a room in cache`() = runTest {
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(sessionId = A_SESSION_ID, roomId = A_ROOM_ID))
        val prismClient = FakePRISMClient(A_SESSION_ID).apply {
            givenGetRoomResult(A_ROOM_ID, room)
        }
        val flowFactory = LoadingRoomStateFlowFactory(prismClient)
        flowFactory
            .create(lifecycleScope = this, roomId = A_ROOM_ID, joinedRoom = null)
            .test {
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loading)
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loaded(room))
            }
    }

    @Test
    fun `flow should emit Loading and then Loaded when there is a room in cache after SS is loaded`() = runTest {
        val room = FakeJoinedRoom(baseRoom = FakeBaseRoom(sessionId = A_SESSION_ID, roomId = A_ROOM_ID))
        val roomList = FakeDynamicRoomList()
        val roomListService = FakeRoomListService(allRooms = roomList)
        val prismClient = FakePRISMClient(A_SESSION_ID, roomListService = roomListService)
        val flowFactory = LoadingRoomStateFlowFactory(prismClient)
        flowFactory
            .create(lifecycleScope = this, roomId = A_ROOM_ID, joinedRoom = null)
            .test {
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loading)
                prismClient.givenGetRoomResult(A_ROOM_ID, room)
                roomList.loadingState.emit(RoomList.LoadingState.Loaded(1))
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loaded(room))
            }
    }

    @Test
    fun `flow should emit Loading and then Error when there is no room in cache after SS is loaded`() = runTest {
        val roomList = FakeDynamicRoomList()
        val roomListService = FakeRoomListService(allRooms = roomList)
        val prismClient = FakePRISMClient(A_SESSION_ID, roomListService = roomListService)
        val flowFactory = LoadingRoomStateFlowFactory(prismClient)
        flowFactory
            .create(lifecycleScope = this, roomId = A_ROOM_ID, joinedRoom = null)
            .test {
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Loading)
                roomList.loadingState.emit(RoomList.LoadingState.Loaded(1))
                assertThat(awaitItem()).isEqualTo(LoadingRoomState.Error)
            }
    }
}
