/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.di

import androidx.annotation.VisibleForTesting
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.androidutils.hash.hash
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.PRISMClientProvider
import io.prism.android.libraries.prism.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.services.analytics.api.AnalyticsService
import io.prism.android.services.analyticsproviders.api.AnalyticsUserData
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

private const val SAVE_INSTANCE_KEY = "io.prism.android.x.di.PRISMClientsHolder.SaveInstanceKey"

/**
 * In-memory cache for logged in PRISM sessions.
 *
 * This component contains both the [PRISMClient] and the [SyncOrchestrator] for each session.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PRISMSessionCache(
    private val authenticationService: PRISMAuthenticationService,
    private val syncOrchestratorFactory: SyncOrchestrator.Factory,
    private val analyticsService: AnalyticsService,
) : PRISMClientProvider {
    private val sessionIdsToPRISMSession = ConcurrentHashMap<SessionId, InMemoryPRISMSession>()
    private val restoreMutex = Mutex()

    init {
        authenticationService.listenToNewPRISMClients { prismClient ->
            onNewPRISMClient(prismClient)
        }
    }

    fun removeAll() {
        sessionIdsToPRISMSession.clear()
    }

    fun remove(sessionId: SessionId) {
        sessionIdsToPRISMSession.remove(sessionId)
    }

    override fun getOrNull(sessionId: SessionId): PRISMClient? {
        return sessionIdsToPRISMSession[sessionId]?.prismClient
    }

    override suspend fun getOrRestore(sessionId: SessionId): Result<PRISMClient> {
        return restoreMutex.withLock {
            when (val cached = getOrNull(sessionId)) {
                null -> restore(sessionId)
                else -> Result.success(cached)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getSyncOrchestrator(sessionId: SessionId): SyncOrchestrator? {
        return sessionIdsToPRISMSession[sessionId]?.syncOrchestrator
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun restoreWithSavedState(state: SavedStateMap?) {
        Timber.d("Restore state")
        if (state == null || sessionIdsToPRISMSession.isNotEmpty()) {
            Timber.w("No need to restore saved state")
            return
        }
        val sessionIds = state[SAVE_INSTANCE_KEY] as? Array<SessionId>
        Timber.d("Restore prism session keys = ${sessionIds?.map { it.value }}")
        if (sessionIds.isNullOrEmpty()) return
        // Not ideal but should only happens in case of process recreation. This ensure we restore all the active sessions before restoring the node graphs.
        sessionIds.forEach { sessionId ->
            getOrRestore(sessionId)
        }
    }

    fun saveIntoSavedState(state: MutableSavedStateMap) {
        val sessionKeys = sessionIdsToPRISMSession.keys.toTypedArray()
        Timber.d("Save prism session keys = ${sessionKeys.map { it.value }}")
        state[SAVE_INSTANCE_KEY] = sessionKeys
    }

    private suspend fun restore(sessionId: SessionId): Result<PRISMClient> {
        Timber.d("Restore prism session: $sessionId")
        return authenticationService.restoreSession(sessionId)
            .onSuccess { prismClient ->
                // Add the current homeserver (hashed) to the extra info
                // This may not play well with multiple sessions, but it should work for now
                analyticsService.addIndexableData(AnalyticsUserData.HOMESERVER, prismClient.userIdServerName().hash())

                // Add the new client to the in-memory cache
                onNewPRISMClient(prismClient)
            }
            .onFailure {
                Timber.e(it, "Fail to restore session")
            }
    }

    private fun onNewPRISMClient(prismClient: PRISMClient) {
        val syncOrchestrator = syncOrchestratorFactory.create(
            syncService = prismClient.syncService,
            sessionCoroutineScope = prismClient.sessionCoroutineScope,
        )
        sessionIdsToPRISMSession[prismClient.sessionId] = InMemoryPRISMSession(
            prismClient = prismClient,
            syncOrchestrator = syncOrchestrator,
        )
        syncOrchestrator.start()
    }
}

private data class InMemoryPRISMSession(
    val prismClient: PRISMClient,
    val syncOrchestrator: SyncOrchestrator,
)
