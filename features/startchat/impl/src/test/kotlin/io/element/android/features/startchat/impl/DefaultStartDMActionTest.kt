/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl

import androidx.compose.runtime.mutableStateOf
import com.google.common.truth.Truth.assertThat
import uk.fathertkt.prism.features.analytics.plan.CreatedRoom
import io.prism.android.features.startchat.api.ConfirmingStartDmWithPRISMUser
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.services.analytics.api.AnalyticsService
import io.prism.android.services.analytics.test.FakeAnalyticsService
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultStartDMActionTest {
    @Test
    fun `when dm is found, assert state is updated with given room id`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenFindDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(prismClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aPRISMUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Success(A_ROOM_ID))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when finding the dm fails, assert state is updated with given error`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenFindDmResult(Result.failure(AN_EXCEPTION))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(prismClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aPRISMUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when dm is not found, assert dm is created, state is updated with given room id and analytics get called`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(prismClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aPRISMUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Success(A_ROOM_ID))
        assertThat(analyticsService.capturedEvents).containsExactly(CreatedRoom(isDM = true))
    }

    @Test
    fun `when dm is not found, and createIfDmDoesNotExist is false, assert dm is not created and state is updated to confirmation state`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(prismClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        val prismUser = aPRISMUser()
        action.execute(prismUser, false, state)
        assertThat(state.value).isEqualTo(ConfirmingStartDmWithPRISMUser(prismUser))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when dm creation fails, assert state is updated with given error`() = runTest {
        val prismClient = FakePRISMClient().apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.failure(AN_EXCEPTION))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(prismClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aPRISMUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    private fun createStartDMAction(
        prismClient: PRISMClient = FakePRISMClient(),
        analyticsService: AnalyticsService = FakeAnalyticsService(),
    ): DefaultStartDMAction {
        return DefaultStartDMAction(
            prismClient = prismClient,
            analyticsService = analyticsService,
        )
    }
}
