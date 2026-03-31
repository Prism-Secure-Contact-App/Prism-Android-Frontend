/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.tasks

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.invite.test.InMemorySeenInvitesStore
import io.prism.android.features.preferences.impl.DefaultCacheService
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.room.FakeJoinedRoom
import io.prism.android.libraries.push.test.FakePushService
import io.prism.android.services.appnavstate.impl.DefaultActiveRoomsHolder
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultClearCacheUseCaseTest {
    @Test
    fun `execute clear cache should do all the expected tasks`() = runTest {
        val activeRoomsHolder = DefaultActiveRoomsHolder().apply { addRoom(FakeJoinedRoom()) }
        val clearCacheLambda = lambdaRecorder<Unit> { }
        val prismClient = FakePRISMClient(
            sessionId = A_SESSION_ID,
            clearCacheLambda = clearCacheLambda,
        )
        val defaultCacheService = DefaultCacheService()
        val setIgnoreRegistrationErrorLambda = lambdaRecorder<SessionId, Boolean, Unit> { _, _ -> }
        val resetBatteryOptimizationStateResult = lambdaRecorder<Unit> { }
        val pushService = FakePushService(
            setIgnoreRegistrationErrorLambda = setIgnoreRegistrationErrorLambda,
            resetBatteryOptimizationStateResult = resetBatteryOptimizationStateResult,
        )
        val seenInvitesStore = InMemorySeenInvitesStore(setOf(A_ROOM_ID))
        assertThat(seenInvitesStore.seenRoomIds().first()).isNotEmpty()
        val sut = DefaultClearCacheUseCase(
            context = InstrumentationRegistry.getInstrumentation().context,
            prismClient = prismClient,
            coroutineDispatchers = testCoroutineDispatchers(),
            defaultCacheService = defaultCacheService,
            okHttpClient = { OkHttpClient.Builder().build() },
            pushService = pushService,
            seenInvitesStore = seenInvitesStore,
            activeRoomsHolder = activeRoomsHolder,
        )
        defaultCacheService.clearedCacheEventFlow.test {
            sut.invoke()
            clearCacheLambda.assertions().isCalledOnce()
            setIgnoreRegistrationErrorLambda.assertions().isCalledOnce()
                .with(value(prismClient.sessionId), value(false))
            resetBatteryOptimizationStateResult.assertions().isCalledOnce()
            assertThat(awaitItem()).isEqualTo(prismClient.sessionId)
            assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
            assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNull()
        }
    }
}
