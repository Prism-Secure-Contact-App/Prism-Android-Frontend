/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.prism.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.prism.api.auth.external.ExternalSession
import io.prism.android.libraries.prism.api.verification.SessionVerifiedStatus
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.auth.FakePRISMAuthenticationService
import io.prism.android.libraries.prism.test.core.aBuildMeta
import io.prism.android.libraries.prism.test.verification.FakeSessionVerificationService
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CreateAccountPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.url).isEqualTo("aUrl")
            assertThat(initialState.pageProgress).isEqualTo(0)
            assertThat(initialState.createAction).isEqualTo(AsyncAction.Uninitialized)
            assertThat(initialState.isDebugBuild).isTrue()
        }
    }

    @Test
    fun `present - set up progress update the state`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.SetPageProgress(33))
            assertThat(awaitItem().pageProgress).isEqualTo(33)
        }
    }

    @Test
    fun `present - receiving a message not able to be parsed change the state to error`() = runTest {
        val presenter = createPresenter(
            messageParser = FakeMessageParser { error("An error") }
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived(""))
            assertThat(awaitItem().createAction).isInstanceOf(AsyncAction.Failure::class.java)
        }
    }

    @Test
    fun `present - receiving a message containing isTrusted is ignored`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived("isTrusted"))
        }
    }

    @Test
    fun `present - receiving a message able to be parsed change the state to success`() = runTest {
        val lambda = lambdaRecorder<String, ExternalSession> { _ -> anExternalSession() }
        val sessionVerificationService = FakeSessionVerificationService()
        val presenter = createPresenter(
            authenticationService = FakePRISMAuthenticationService(
                importCreatedSessionLambda = { Result.success(A_SESSION_ID) }
            ),
            messageParser = FakeMessageParser(lambda),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived("aMessage"))
            assertThat(awaitItem().createAction.isLoading()).isTrue()
            sessionVerificationService.emitVerifiedStatus(SessionVerifiedStatus.Verified)
            assertThat(awaitItem().createAction.dataOrNull()).isEqualTo(A_SESSION_ID)
        }
        lambda.assertions().isCalledOnce().with(value("aMessage"))
    }

    @Test
    fun `present - receiving a message able to be parsed but error in importing change the state to error`() = runTest {
        val presenter = createPresenter(
            authenticationService = FakePRISMAuthenticationService(
                importCreatedSessionLambda = { Result.failure(AN_EXCEPTION) }
            ),
            messageParser = FakeMessageParser { anExternalSession() }
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived(""))
            assertThat(awaitItem().createAction.isLoading()).isTrue()
            assertThat(awaitItem().createAction.errorOrNull()).isNotNull()
        }
    }

    private fun createPresenter(
        url: String = "aUrl",
        authenticationService: PRISMAuthenticationService = FakePRISMAuthenticationService(),
        messageParser: MessageParser = FakeMessageParser(),
        buildMeta: BuildMeta = aBuildMeta(),
    ) = CreateAccountPresenter(
        url = url,
        authenticationService = authenticationService,
        messageParser = messageParser,
        buildMeta = buildMeta,
    )
}
