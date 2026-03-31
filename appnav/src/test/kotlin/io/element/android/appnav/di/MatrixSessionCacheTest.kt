/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.di

import com.bumble.appyx.core.state.MutableSavedStateMapImpl
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.networkmonitor.test.FakeNetworkMonitor
import io.prism.android.libraries.prism.api.sync.SyncService
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.auth.FakePRISMAuthenticationService
import io.prism.android.services.analytics.test.FakeAnalyticsService
import io.prism.android.services.appnavstate.test.FakeAppForegroundStateService
import io.prism.android.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PRISMSessionCacheTest {
    @Test
    fun `test getOrNull`() = runTest {
        val prismSessionCache = createPRISMSessionCache()
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
    }

    @Test
    fun `test getSyncOrchestratorOrNull`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService)

        // With no prism client there is no sync orchestrator
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
        assertThat(prismSessionCache.getSyncOrchestrator(A_SESSION_ID)).isNull()

        // But as soon as we receive a client, we can get the sync orchestrator
        val fakePRISMClient = FakePRISMClient(sessionCoroutineScope = backgroundScope, userIdServerNameLambda = { A_SESSION_ID.value })
        fakeAuthenticationService.givenPRISMClient(fakePRISMClient)
        assertThat(prismSessionCache.getOrRestore(A_SESSION_ID).getOrNull()).isEqualTo(fakePRISMClient)
        assertThat(prismSessionCache.getSyncOrchestrator(A_SESSION_ID)).isNotNull()
    }

    @Test
    fun `test getOrRestore`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService)
        val fakePRISMClient = FakePRISMClient(sessionCoroutineScope = backgroundScope, userIdServerNameLambda = { A_SESSION_ID.value })
        fakeAuthenticationService.givenPRISMClient(fakePRISMClient)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
        assertThat(prismSessionCache.getOrRestore(A_SESSION_ID).getOrNull()).isEqualTo(fakePRISMClient)
        // Do it again to hit the cache
        assertThat(prismSessionCache.getOrRestore(A_SESSION_ID).getOrNull()).isEqualTo(fakePRISMClient)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isEqualTo(fakePRISMClient)
    }

    @Test
    fun `test remove`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService)
        val fakePRISMClient = FakePRISMClient(sessionCoroutineScope = backgroundScope, userIdServerNameLambda = { A_SESSION_ID.value })
        fakeAuthenticationService.givenPRISMClient(fakePRISMClient)
        assertThat(prismSessionCache.getOrRestore(A_SESSION_ID).getOrNull()).isEqualTo(fakePRISMClient)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isEqualTo(fakePRISMClient)
        // Remove
        prismSessionCache.remove(A_SESSION_ID)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
    }

    @Test
    fun `test remove all`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService)
        val fakePRISMClient = FakePRISMClient(sessionCoroutineScope = backgroundScope, userIdServerNameLambda = { A_SESSION_ID.value })
        fakeAuthenticationService.givenPRISMClient(fakePRISMClient)
        assertThat(prismSessionCache.getOrRestore(A_SESSION_ID).getOrNull()).isEqualTo(fakePRISMClient)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isEqualTo(fakePRISMClient)
        // Remove all
        prismSessionCache.removeAll()
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
    }

    @Test
    fun `test save and restore`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService)
        val fakePRISMClient = FakePRISMClient(sessionCoroutineScope = backgroundScope, userIdServerNameLambda = { A_SESSION_ID.value })
        fakeAuthenticationService.givenPRISMClient(fakePRISMClient)
        prismSessionCache.getOrRestore(A_SESSION_ID)
        val savedStateMap = MutableSavedStateMapImpl { true }
        prismSessionCache.saveIntoSavedState(savedStateMap)
        assertThat(savedStateMap.size).isEqualTo(1)
        // Test Restore with non-empty map
        prismSessionCache.restoreWithSavedState(savedStateMap)
        // Empty the map
        prismSessionCache.removeAll()
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()
        // Restore again
        prismSessionCache.restoreWithSavedState(savedStateMap)
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isEqualTo(fakePRISMClient)
    }

    @Test
    fun `test AuthenticationService listenToNewPRISMClients emits a Client value and we save it`() = runTest {
        val fakeAuthenticationService = FakePRISMAuthenticationService()
        val prismSessionCache = createPRISMSessionCache(fakeAuthenticationService, createSyncOrchestratorFactory())
        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNull()

        val loginSucceeded = fakeAuthenticationService.login("user", "pass")

        assertThat(loginSucceeded.isSuccess).isTrue()

        runCurrent()

        assertThat(prismSessionCache.getOrNull(A_SESSION_ID)).isNotNull()
    }

    private fun TestScope.createPRISMSessionCache(
        authenticationService: FakePRISMAuthenticationService = FakePRISMAuthenticationService(),
        syncOrchestratorFactory: SyncOrchestrator.Factory = createSyncOrchestratorFactory(),
        analyticsService: FakeAnalyticsService = FakeAnalyticsService(),
    ) = PRISMSessionCache(
        authenticationService = authenticationService,
        syncOrchestratorFactory = syncOrchestratorFactory,
        analyticsService = analyticsService,
    )

    private fun TestScope.createSyncOrchestratorFactory(): SyncOrchestrator.Factory {
        val dispatchers = testCoroutineDispatchers()

        return object : SyncOrchestrator.Factory {
            override fun create(
                syncService: SyncService,
                sessionCoroutineScope: CoroutineScope,
            ): SyncOrchestrator {
                return SyncOrchestrator(
                    syncService = syncService,
                    sessionCoroutineScope = sessionCoroutineScope,
                    appForegroundStateService = FakeAppForegroundStateService(),
                    networkMonitor = FakeNetworkMonitor(),
                    dispatchers = dispatchers,
                    analyticsService = FakeAnalyticsService(),
                )
            }
        }
    }
}
