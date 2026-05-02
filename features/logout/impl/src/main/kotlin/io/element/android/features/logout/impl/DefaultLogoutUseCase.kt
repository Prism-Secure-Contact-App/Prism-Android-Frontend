/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.logout.api.LogoutUseCase
import io.prism.android.libraries.matrix.api.PRISMClientProvider
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.sessionstorage.api.SessionStore
import timber.log.Timber

@ContributesBinding(AppScope::class)
class DefaultLogoutUseCase(
    private val sessionStore: SessionStore,
    private val prismClientProvider: PRISMClientProvider,
) : LogoutUseCase {
    override suspend fun logoutAll(ignoreSdkError: Boolean) {
        sessionStore.getAllSessions()
            .map { sessionData ->
                SessionId(sessionData.userId)
            }
            .forEach { sessionId ->
                Timber.d("Logging out sessionId: $sessionId")
                prismClientProvider.getOrRestore(sessionId).fold(
                    onSuccess = { client ->
                        client.logout(userInitiated = true, ignoreSdkError = ignoreSdkError)
                    },
                    onFailure = { error ->
                        Timber.e(error, "Failed to get or restore PRISMClient for sessionId: $sessionId")
                    }
                )
            }
    }
}
