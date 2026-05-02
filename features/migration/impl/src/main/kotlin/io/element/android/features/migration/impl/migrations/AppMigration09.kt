/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.migration.impl.migrations

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.prism.android.libraries.matrix.api.PRISMClientProvider
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.sessionstorage.api.SessionStore

/**
 * Ensure we clear the well-known cached config, since it could be invalid due to an SDK issue.
 */
@ContributesIntoSet(AppScope::class)
class AppMigration09(
    private val sessionStore: SessionStore,
    private val prismClientProvider: PRISMClientProvider,
) : AppMigration {
    override val order: Int = 9

    override suspend fun migrate(isFreshInstall: Boolean) {
        if (isFreshInstall) return

        val sessions = sessionStore.getAllSessions()

        for (session in sessions) {
            val client = prismClientProvider.getOrRestore(SessionId(session.userId)).getOrNull() ?: continue
            client.resetWellKnownConfig()
        }
    }
}
