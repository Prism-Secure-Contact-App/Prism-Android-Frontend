/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.migration.impl.migrations

import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.FakePRISMClientProvider
import io.prism.android.libraries.sessionstorage.test.InMemorySessionStore
import io.prism.android.libraries.sessionstorage.test.aSessionData
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AppMigration09Test {
    @Test
    fun `migration on fresh install does nothing`() = runTest {
        val sessionStore = InMemorySessionStore(initialList = listOf(aSessionData()))
        val getClientLambda = lambdaRecorder<SessionId, Result<PRISMClient>> { Result.success(FakePRISMClient()) }
        val clientProvider = FakePRISMClientProvider(getClient = getClientLambda)
        val migration = AppMigration09(sessionStore, clientProvider)
        migration.migrate(isFreshInstall = true)

        getClientLambda.assertions().isNeverCalled()
    }

    @Test
    fun `migration on upgrade should invoke the resetWellKnownConfig method`() = runTest {
        val sessionStore = InMemorySessionStore(initialList = listOf(aSessionData()))
        val resetWellKnownLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val getClientLambda = lambdaRecorder<SessionId, Result<PRISMClient>> {
            Result.success(FakePRISMClient(resetWellKnownConfigLambda = resetWellKnownLambda))
        }
        val clientProvider = FakePRISMClientProvider(getClient = getClientLambda)
        val migration = AppMigration09(sessionStore, clientProvider)
        migration.migrate(isFreshInstall = false)

        getClientLambda.assertions().isCalledOnce()
        resetWellKnownLambda.assertions().isCalledOnce()
    }
}
