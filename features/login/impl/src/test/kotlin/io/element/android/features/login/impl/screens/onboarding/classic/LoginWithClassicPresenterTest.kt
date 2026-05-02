/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.prism.android.features.login.impl.screens.onboarding.classic

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.featureflag.test.FakeFeatureFlagService
import io.prism.android.libraries.matrix.test.A_SECRET
import io.prism.android.libraries.matrix.test.A_USER_ID
import io.prism.android.libraries.sessionstorage.api.SessionStore
import io.prism.android.libraries.sessionstorage.test.InMemorySessionStore
import io.prism.android.libraries.sessionstorage.test.aSessionData
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoginWithClassicPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state - feature disabled - start is not invoked`() = runTest {
        val presenter = createPresenter(
            prismClassicConnection = FakePRISMClassicConnection(
                startResult = {
                    error("start should not be invoked when feature is disabled")
                },
            )
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.canLoginWithClassic).isFalse()
            assertThat(initialState.loginWithClassicAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - feature enabled - start is invoked`() = runTest {
        val startResult = lambdaRecorder<Unit> {}
        val presenter = createPresenter(
            prismClassicConnection = FakePRISMClassicConnection(
                startResult = startResult,
            ),
            isFeatureEnabled = true,
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.canLoginWithClassic).isFalse()
            assertThat(initialState.loginWithClassicAction.isUninitialized()).isTrue()
            val finalState = awaitItem()
            assertThat(finalState.canLoginWithClassic).isFalse()
        }
        startResult.assertions().isCalledOnce()
    }

    @Test
    fun `present - emit request data invokes the expected method`() = runTest {
        val requestDataResult = lambdaRecorder<Unit> {}
        val presenter = createPresenter(
            prismClassicConnection = FakePRISMClassicConnection(
                startResult = {},
                requestDataResult = requestDataResult,
            ),
            isFeatureEnabled = true,
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.canLoginWithClassic).isFalse()
            assertThat(initialState.loginWithClassicAction.isUninitialized()).isTrue()
            val nextState = awaitItem()
            assertThat(nextState.canLoginWithClassic).isFalse()
            nextState.eventSink(LoginWithClassicEvent.RefreshData)
        }
        requestDataResult.assertions().isCalledOnce()
    }

    @Test
    fun `present - start login with wrong state emits an error`() = runTest {
        val presenter = createPresenter(
            prismClassicConnection = FakePRISMClassicConnection(
                startResult = {},
            ),
            isFeatureEnabled = true,
        )
        presenter.test {
            skipItems(1)
            val state = awaitItem()
            state.eventSink(LoginWithClassicEvent.StartLoginWithClassic)
            val errorState = awaitItem()
            assertThat(errorState.loginWithClassicAction.isFailure()).isTrue()
        }
    }

    @Test
    fun `present - start login with correct state - user cancel`() = runTest {
        val prismClassicConnection = FakePRISMClassicConnection(
            startResult = {},
        )
        val presenter = createPresenter(
            prismClassicConnection = prismClassicConnection,
            isFeatureEnabled = true,
        )
        presenter.test {
            skipItems(2)
            prismClassicConnection.emitState(
                PRISMClassicConnectionState.PRISMClassicReady(userId = A_USER_ID, secrets = A_SECRET)
            )
            val readyState = awaitItem()
            assertThat(readyState.canLoginWithClassic).isTrue()
            readyState.eventSink(LoginWithClassicEvent.StartLoginWithClassic)
            val confirmingState = awaitItem()
            assertThat(confirmingState.loginWithClassicAction.isConfirming()).isTrue()
            assertThat((confirmingState.loginWithClassicAction as ConfirmingLoginWithPRISMClassic).userId).isEqualTo(A_USER_ID)
            confirmingState.eventSink(LoginWithClassicEvent.CloseDialog)
            val finalState = awaitItem()
            assertThat(finalState.loginWithClassicAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - start login with correct state - user confirms`() = runTest {
        val prismClassicConnection = FakePRISMClassicConnection(
            startResult = {},
        )
        val presenter = createPresenter(
            prismClassicConnection = prismClassicConnection,
            isFeatureEnabled = true,
        )
        presenter.test {
            skipItems(2)
            prismClassicConnection.emitState(
                PRISMClassicConnectionState.PRISMClassicReady(userId = A_USER_ID, secrets = A_SECRET)
            )
            val readyState = awaitItem()
            assertThat(readyState.canLoginWithClassic).isTrue()
            readyState.eventSink(LoginWithClassicEvent.StartLoginWithClassic)
            val confirmingState = awaitItem()
            assertThat(confirmingState.loginWithClassicAction.isConfirming()).isTrue()
            assertThat((confirmingState.loginWithClassicAction as ConfirmingLoginWithPRISMClassic).userId).isEqualTo(A_USER_ID)
            confirmingState.eventSink(LoginWithClassicEvent.DoLoginWithClassic)
            val loadingState = awaitItem()
            assertThat(loadingState.loginWithClassicAction.isLoading()).isTrue()
            val finalState = awaitItem()
            assertThat(finalState.loginWithClassicAction.isSuccess()).isTrue()
        }
    }

    @Test
    fun `present - cannot sign in if a session with the same account already exists`() = runTest {
        val prismClassicConnection = FakePRISMClassicConnection(
            startResult = {},
        )
        val presenter = createPresenter(
            prismClassicConnection = prismClassicConnection,
            isFeatureEnabled = true,
            sessionStore = InMemorySessionStore(
                initialList = listOf(
                    aSessionData(
                        sessionId = A_USER_ID.value,
                    )
                )
            ),
        )
        presenter.test {
            skipItems(2)
            prismClassicConnection.emitState(
                PRISMClassicConnectionState.PRISMClassicReady(userId = A_USER_ID, secrets = A_SECRET)
            )
            // No new item, because canLoginWithClassic is still false
        }
    }

    @Test
    fun `present - cannot sign in if the feature is disabled`() = runTest {
        val prismClassicConnection = FakePRISMClassicConnection()
        val presenter = createPresenter(
            prismClassicConnection = prismClassicConnection,
            isFeatureEnabled = false,
        )
        presenter.test {
            skipItems(1)
            // Note: it should not happen IRL
            prismClassicConnection.emitState(
                PRISMClassicConnectionState.PRISMClassicReady(userId = A_USER_ID, secrets = A_SECRET)
            )
            // No new item, because canLoginWithClassic is still false
        }
    }
}

private fun createPresenter(
    prismClassicConnection: PRISMClassicConnection = FakePRISMClassicConnection(),
    sessionStore: SessionStore = InMemorySessionStore(),
    isFeatureEnabled: Boolean = false,
    featureFlagService: FeatureFlagService = FakeFeatureFlagService(
        initialState = mapOf(FeatureFlags.SignInWithClassic.key to isFeatureEnabled)
    ),
) = LoginWithClassicPresenter(
    prismClassicConnection = prismClassicConnection,
    sessionStore = sessionStore,
    featureFlagService = featureFlagService,
)
