/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.alias.ResolvedRoomAlias
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_ROOM_ALIAS
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_SERVER_LIST
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.tests.testutils.WarmUpRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Optional

class RoomAliasHelperPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            assertThat(awaitItem().resolveState.isUninitialized()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - resolve alias to roomId`() = runTest {
        val result = Optional.of(aResolvedRoomAlias())
        val client = FakePRISMClient(
            resolveRoomAliasResult = { Result.success(result) }
        )
        val presenter = createPresenter(prismClient = client)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            assertThat(awaitItem().resolveState.isUninitialized()).isTrue()
            assertThat(awaitItem().resolveState.isLoading()).isTrue()
            val resultState = awaitItem()
            assertThat(resultState.roomAlias).isEqualTo(A_ROOM_ALIAS)
            assertThat(resultState.resolveState.dataOrNull()).isEqualTo(result.get())
        }
    }

    @Test
    fun `present - resolve alias error and retry`() = runTest {
        val client = FakePRISMClient(
            resolveRoomAliasResult = { Result.failure(AN_EXCEPTION) }
        )
        val presenter = createPresenter(prismClient = client)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            assertThat(awaitItem().resolveState.isUninitialized()).isTrue()
            assertThat(awaitItem().resolveState.isLoading()).isTrue()
            val resultState = awaitItem()
            assertThat(resultState.resolveState.errorOrNull()).isEqualTo(AN_EXCEPTION)
            resultState.eventSink(RoomAliasResolverEvents.Retry)
            val retryLoadingState = awaitItem()
            assertThat(retryLoadingState.resolveState.isLoading()).isTrue()
            val retryState = awaitItem()
            assertThat(retryState.resolveState.errorOrNull()).isEqualTo(AN_EXCEPTION)
        }
    }
}

internal fun createPresenter(
    roomAlias: RoomAlias = A_ROOM_ALIAS,
    prismClient: PRISMClient = FakePRISMClient(),
) = RoomAliasResolverPresenter(
    roomAlias = roomAlias,
    prismClient = prismClient,
)

internal fun aResolvedRoomAlias(
    roomId: RoomId = A_ROOM_ID,
    servers: List<String> = A_SERVER_LIST,
) = ResolvedRoomAlias(
    roomId = roomId,
    servers = servers,
)
