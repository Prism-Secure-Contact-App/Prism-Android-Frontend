/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.prism.android.features.logout.impl

import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_ID_2
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.FakePRISMClientProvider
import io.prism.android.libraries.sessionstorage.test.InMemorySessionStore
import io.prism.android.libraries.sessionstorage.test.aSessionData
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultLogoutUseCaseTest {
    @Test
    fun `test logout from one session`() = runTest {
        val logoutLambda1 = lambdaRecorder<Boolean, Boolean, Unit> { _, _ -> }
        val client1 = FakePRISMClient(A_USER_ID).apply {
            logoutLambda = logoutLambda1
        }
        val sut = DefaultLogoutUseCase(
            sessionStore = InMemorySessionStore(
                initialList = listOf(
                    aSessionData(sessionId = A_USER_ID.value),
                )
            ),
            prismClientProvider = FakePRISMClientProvider(
                getClient = { sessionId ->
                    when (sessionId) {
                        A_USER_ID -> Result.success(client1)
                        else -> error("Unexpected sessionId")
                    }
                }
            ),
        )
        sut.logoutAll(ignoreSdkError = true)
        logoutLambda1.assertions().isCalledOnce().with(value(true), value(true))
    }

    @Test
    fun `test logout from several sessions`() = runTest {
        val logoutLambda1 = lambdaRecorder<Boolean, Boolean, Unit> { _, _ -> }
        val logoutLambda2 = lambdaRecorder<Boolean, Boolean, Unit> { _, _ -> }
        val client1 = FakePRISMClient(A_USER_ID).apply {
            logoutLambda = logoutLambda1
        }
        val client2 = FakePRISMClient(A_USER_ID_2).apply {
            logoutLambda = logoutLambda2
        }
        val sut = DefaultLogoutUseCase(
            sessionStore = InMemorySessionStore(
                initialList = listOf(
                    aSessionData(sessionId = A_USER_ID.value),
                    aSessionData(sessionId = A_USER_ID_2.value),
                )
            ),
            prismClientProvider = FakePRISMClientProvider(
                getClient = { sessionId ->
                    when (sessionId) {
                        A_USER_ID -> Result.success(client1)
                        A_USER_ID_2 -> Result.success(client2)
                        else -> error("Unexpected sessionId")
                    }
                }
            ),
        )
        sut.logoutAll(ignoreSdkError = true)
        logoutLambda1.assertions().isCalledOnce().with(value(true), value(true))
        logoutLambda2.assertions().isCalledOnce().with(value(true), value(true))
    }

    @Test
    fun `test logout session not found is ignored`() = runTest {
        val sut = DefaultLogoutUseCase(
            sessionStore = InMemorySessionStore(
                initialList = listOf(
                    aSessionData(sessionId = A_USER_ID.value),
                )
            ),
            prismClientProvider = FakePRISMClientProvider(
                getClient = { sessionId ->
                    when (sessionId) {
                        A_USER_ID -> Result.failure(Exception("Session not found"))
                        else -> error("Unexpected sessionId")
                    }
                }
            ),
        )
        sut.logoutAll(ignoreSdkError = true)
        // No error
    }

    @Test
    fun `test logout no sessions`() = runTest {
        val sut = DefaultLogoutUseCase(
            sessionStore = InMemorySessionStore(
                initialList = emptyList()
            ),
            prismClientProvider = FakePRISMClientProvider(
                getClient = { sessionId ->
                    when (sessionId) {
                        else -> error("Unexpected sessionId")
                    }
                }
            ),
        )
        sut.logoutAll(ignoreSdkError = true)
        // No error
    }
}
