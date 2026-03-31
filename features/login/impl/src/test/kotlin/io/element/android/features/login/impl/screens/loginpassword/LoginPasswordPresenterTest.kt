/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.loginpassword

import com.google.common.truth.Truth.assertThat
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.features.enterprise.test.FakeEnterpriseService
import io.prism.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_PASSWORD
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.A_USER_NAME
import io.prism.android.libraries.prism.test.auth.FakePRISMAuthenticationService
import io.prism.android.libraries.prism.test.auth.aPRISMHomeServerDetails
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoginPasswordPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        createLoginPasswordPresenter().test {
            val initialState = awaitItem()
            assertThat(initialState.accountProvider.url).isEqualTo(AuthenticationConfig.PRISM_ORG_URL)
            assertThat(initialState.formState).isEqualTo(LoginFormState.Default)
            assertThat(initialState.loginAction).isEqualTo(AsyncData.Uninitialized)
            assertThat(initialState.submitEnabled).isFalse()
        }
    }

    @Test
    fun `present - enter login and password`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        createLoginPasswordPresenter(
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(LoginPasswordEvents.SetLogin(A_USER_NAME))
            val loginState = awaitItem()
            assertThat(loginState.formState).isEqualTo(LoginFormState(login = A_USER_NAME, password = ""))
            assertThat(loginState.submitEnabled).isFalse()
            initialState.eventSink.invoke(LoginPasswordEvents.SetPassword(A_PASSWORD))
            val loginAndPasswordState = awaitItem()
            assertThat(loginAndPasswordState.formState).isEqualTo(LoginFormState(login = A_USER_NAME, password = A_PASSWORD))
            assertThat(loginAndPasswordState.submitEnabled).isTrue()
        }
    }

    @Test
    fun `present - submit`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        createLoginPasswordPresenter(
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(LoginPasswordEvents.SetLogin(A_USER_NAME))
            initialState.eventSink.invoke(LoginPasswordEvents.SetPassword(A_PASSWORD))
            skipItems(1)
            val loginAndPasswordState = awaitItem()
            loginAndPasswordState.eventSink.invoke(LoginPasswordEvents.Submit)
            val submitState = awaitItem()
            assertThat(submitState.loginAction).isInstanceOf(AsyncData.Loading::class.java)
            val loggedInState = awaitItem()
            assertThat(loggedInState.loginAction).isEqualTo(AsyncData.Success(A_SESSION_ID))
        }
    }

    @Test
    fun `present - submit with error`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        createLoginPasswordPresenter(
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(LoginPasswordEvents.SetLogin(A_USER_NAME))
            initialState.eventSink.invoke(LoginPasswordEvents.SetPassword(A_PASSWORD))
            skipItems(1)
            val loginAndPasswordState = awaitItem()
            authenticationService.givenLoginError(AN_EXCEPTION)
            loginAndPasswordState.eventSink.invoke(LoginPasswordEvents.Submit)
            val submitState = awaitItem()
            assertThat(submitState.loginAction).isInstanceOf(AsyncData.Loading::class.java)
            val loggedInState = awaitItem()
            assertThat(loggedInState.loginAction).isEqualTo(AsyncData.Failure<SessionId>(AN_EXCEPTION))
        }
    }

    @Test
    fun `present - clear error`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        createLoginPasswordPresenter(
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(LoginPasswordEvents.SetLogin(A_USER_NAME))
            initialState.eventSink.invoke(LoginPasswordEvents.SetPassword(A_PASSWORD))
            skipItems(1)
            val loginAndPasswordState = awaitItem()
            authenticationService.givenLoginError(AN_EXCEPTION)
            loginAndPasswordState.eventSink.invoke(LoginPasswordEvents.Submit)
            val submitState = awaitItem()
            assertThat(submitState.loginAction).isInstanceOf(AsyncData.Loading::class.java)
            val loggedInState = awaitItem()
            // Check an error was returned
            assertThat(loggedInState.loginAction).isEqualTo(AsyncData.Failure<SessionId>(AN_EXCEPTION))
            // Assert the error is then cleared
            loggedInState.eventSink(LoginPasswordEvents.ClearError)
            val clearedState = awaitItem()
            assertThat(clearedState.loginAction).isEqualTo(AsyncData.Uninitialized)
        }
    }

    private fun createLoginPasswordPresenter(
        authenticationService: FakePRISMAuthenticationService = FakePRISMAuthenticationService(),
        accountProviderDataSource: AccountProviderDataSource = AccountProviderDataSource(FakeEnterpriseService()),
    ): LoginPasswordPresenter = LoginPasswordPresenter(
        authenticationService = authenticationService,
        accountProviderDataSource = accountProviderDataSource,
    )
}
