/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.confirmaccountprovider

import com.google.common.truth.Truth.assertThat
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.features.enterprise.test.FakeEnterpriseService
import io.prism.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.prism.android.features.login.impl.login.LoginMode
import io.prism.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.prism.android.features.login.impl.screens.onboarding.createLoginHelper
import io.prism.android.features.login.impl.web.FakeWebClientUrlForAuthenticationRetriever
import io.prism.android.features.login.impl.web.WebClientUrlForAuthenticationRetriever
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.matrix.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.matrix.test.AN_EXCEPTION
import io.prism.android.libraries.matrix.test.auth.FakePRISMAuthenticationService
import io.prism.android.libraries.matrix.test.auth.aPRISMHomeServerDetails
import io.prism.android.libraries.oidc.api.OidcAction
import io.prism.android.libraries.oidc.api.OidcActionFlow
import io.prism.android.libraries.oidc.test.customtab.FakeOidcActionFlow
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ConfirmAccountProviderPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial test`() = runTest {
        val presenter = createConfirmAccountProviderPresenter()
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.isAccountCreation).isFalse()
            assertThat(initialState.submitEnabled).isTrue()
            assertThat(initialState.accountProvider.url).isEqualTo(AuthenticationConfig.PRISM_ORG_URL)
            assertThat(initialState.loginMode).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - continue password login`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsPasswordLogin = true))
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isEqualTo(LoginMode.PasswordLogin)
        }
    }

    @Test
    fun `present - continue oidc`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
        }
    }

    @Test
    fun `present - oidc - cancel with failure`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val defaultOidcActionFlow = FakeOidcActionFlow()
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
            defaultOidcActionFlow = defaultOidcActionFlow,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
            authenticationService.givenOidcCancelError(AN_EXCEPTION)
            defaultOidcActionFlow.post(OidcAction.GoBack())
            val cancelFailureState = awaitItem()
            assertThat(cancelFailureState.loginMode).isInstanceOf(AsyncData.Failure::class.java)
        }
    }

    @Test
    fun `present - oidc - cancel with success`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val defaultOidcActionFlow = FakeOidcActionFlow()
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
            defaultOidcActionFlow = defaultOidcActionFlow,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
            defaultOidcActionFlow.post(OidcAction.GoBack())
            val cancelFinalState = awaitItem()
            assertThat(cancelFinalState.loginMode).isInstanceOf(AsyncData.Uninitialized::class.java)
        }
    }

    @Test
    fun `present - oidc - cancel to unblock`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val defaultOidcActionFlow = FakeOidcActionFlow()
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
            defaultOidcActionFlow = defaultOidcActionFlow,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            defaultOidcActionFlow.post(OidcAction.GoBack(toUnblock = true))
            val cancelFinalState = awaitItem()
            assertThat(cancelFinalState.loginMode).isInstanceOf(AsyncData.Uninitialized::class.java)
        }
    }

    @Test
    fun `present - oidc - success with failure`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val defaultOidcActionFlow = FakeOidcActionFlow()
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
            defaultOidcActionFlow = defaultOidcActionFlow,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
            authenticationService.givenLoginError(AN_EXCEPTION)
            defaultOidcActionFlow.post(OidcAction.Success("aUrl"))
            val cancelLoadingState = awaitItem()
            assertThat(cancelLoadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val cancelFailureState = awaitItem()
            assertThat(cancelFailureState.loginMode).isInstanceOf(AsyncData.Failure::class.java)
        }
    }

    @Test
    fun `present - oidc - success with success`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val defaultOidcActionFlow = FakeOidcActionFlow()
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
            defaultOidcActionFlow = defaultOidcActionFlow,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            val loadingState = awaitItem()
            assertThat(loadingState.submitEnabled).isTrue()
            assertThat(loadingState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.submitEnabled).isFalse()
            assertThat(successState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(successState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
            defaultOidcActionFlow.post(OidcAction.Success("aUrl"))
            val successSuccessState = awaitItem()
            assertThat(successSuccessState.loginMode).isInstanceOf(AsyncData.Loading::class.java)
        }
    }

    @Test
    fun `present - submit fails`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.failure(AN_EXCEPTION)
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink.invoke(ConfirmAccountProviderEvents.Continue)
            skipItems(1) // Loading
            val failureState = awaitItem()
            assertThat(failureState.submitEnabled).isFalse()
            assertThat(failureState.loginMode).isInstanceOf(AsyncData.Failure::class.java)
        }
    }

    @Test
    fun `present - clear error`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.failure(AN_EXCEPTION)
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            prismAuthenticationService = authenticationService,
        )
        presenter.test {
            val initialState = awaitItem()

            // Submit will return an error
            initialState.eventSink(ConfirmAccountProviderEvents.Continue)

            skipItems(1) // Loading

            // Check an error was returned
            val submittedState = awaitItem()
            assertThat(submittedState.loginMode).isInstanceOf(AsyncData.Failure::class.java)

            // Assert the error is then cleared
            submittedState.eventSink(ConfirmAccountProviderEvents.ClearError)
            val clearedState = awaitItem()
            assertThat(clearedState.loginMode).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - confirm account creation without oidc and without url generates an error`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            params = ConfirmAccountProviderPresenter.Params(isAccountCreation = true),
            prismAuthenticationService = authenticationService,
            webClientUrlForAuthenticationRetriever = FakeWebClientUrlForAuthenticationRetriever {
                throw AccountCreationNotSupported()
            },
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(ConfirmAccountProviderEvents.Continue)
            skipItems(1) // Loading
            // Check an error was returned
            val submittedState = awaitItem()
            assertThat(submittedState.loginMode.errorOrNull()).isInstanceOf(AccountCreationNotSupported::class.java)
            // Assert the error is then cleared
            submittedState.eventSink(ConfirmAccountProviderEvents.ClearError)
            val clearedState = awaitItem()
            assertThat(clearedState.loginMode).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - confirm account creation with oidc is successful`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            params = ConfirmAccountProviderPresenter.Params(isAccountCreation = true),
            prismAuthenticationService = authenticationService,
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(ConfirmAccountProviderEvents.Continue)
            skipItems(1) // Loading
            val submittedState = awaitItem()
            assertThat(submittedState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(submittedState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
        }
    }

    @Test
    fun `present - confirm account creation with oidc and url continues with oidc`() = runTest {
        val aUrl = "aUrl"
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            params = ConfirmAccountProviderPresenter.Params(isAccountCreation = true),
            prismAuthenticationService = authenticationService,
            webClientUrlForAuthenticationRetriever = FakeWebClientUrlForAuthenticationRetriever { aUrl },
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(ConfirmAccountProviderEvents.Continue)
            skipItems(1) // Loading
            val submittedState = awaitItem()
            assertThat(submittedState.loginMode).isInstanceOf(AsyncData.Success::class.java)
            assertThat(submittedState.loginMode.dataOrNull()).isInstanceOf(LoginMode.Oidc::class.java)
        }
    }

    @Test
    fun `present - confirm account creation without oidc and with url continuing with url`() = runTest {
        val aUrl = "aUrl"
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        val presenter = createConfirmAccountProviderPresenter(
            params = ConfirmAccountProviderPresenter.Params(isAccountCreation = true),
            prismAuthenticationService = authenticationService,
            webClientUrlForAuthenticationRetriever = FakeWebClientUrlForAuthenticationRetriever { aUrl },
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(ConfirmAccountProviderEvents.Continue)
            skipItems(1) // Loading
            val submittedState = awaitItem()
            assertThat(submittedState.loginMode.dataOrNull()).isEqualTo(LoginMode.AccountCreation(aUrl))
        }
    }

    private fun createConfirmAccountProviderPresenter(
        params: ConfirmAccountProviderPresenter.Params = ConfirmAccountProviderPresenter.Params(isAccountCreation = false),
        accountProviderDataSource: AccountProviderDataSource = AccountProviderDataSource(FakeEnterpriseService()),
        prismAuthenticationService: PRISMAuthenticationService = FakePRISMAuthenticationService(),
        defaultOidcActionFlow: OidcActionFlow = FakeOidcActionFlow(),
        webClientUrlForAuthenticationRetriever: WebClientUrlForAuthenticationRetriever = FakeWebClientUrlForAuthenticationRetriever(),
    ) = ConfirmAccountProviderPresenter(
        params = params,
        accountProviderDataSource = accountProviderDataSource,
        loginHelper = createLoginHelper(
            authenticationService = prismAuthenticationService,
            oidcActionFlow = defaultOidcActionFlow,
            webClientUrlForAuthenticationRetriever = webClientUrlForAuthenticationRetriever,
        ),
    )
}
