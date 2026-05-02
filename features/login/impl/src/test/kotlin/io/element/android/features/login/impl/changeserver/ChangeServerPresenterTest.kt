/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.changeserver

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.enterprise.test.FakeEnterpriseService
import io.prism.android.features.login.impl.accesscontrol.DefaultAccountProviderAccessControl
import io.prism.android.features.login.impl.accountprovider.AccountProvider
import io.prism.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.prism.android.features.login.impl.error.ChangeServerError
import io.prism.android.features.wellknown.test.FakeWellknownRetriever
import io.prism.android.features.wellknown.test.anPRISMWellKnown
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.core.uri.ensureProtocol
import io.prism.android.libraries.matrix.test.AN_EXCEPTION
import io.prism.android.libraries.matrix.test.A_HOMESERVER_URL
import io.prism.android.libraries.matrix.test.auth.FakePRISMAuthenticationService
import io.prism.android.libraries.matrix.test.auth.aPRISMHomeServerDetails
import io.prism.android.libraries.wellknown.api.PRISMWellKnown
import io.prism.android.libraries.wellknown.api.WellknownRetriever
import io.prism.android.libraries.wellknown.api.WellknownRetrieverResult
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChangeServerPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        createPresenter().test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - change server ok`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails(supportsOidcLogin = true))
            },
        )
        createPresenter(
            authenticationService = authenticationService,
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.changeServerAction).isEqualTo(AsyncData.Success(Unit))
        }
    }

    @Test
    fun `present - change server error`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.failure(AN_EXCEPTION)
            },
        )
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(failureState.changeServerAction).isInstanceOf(AsyncData.Failure::class.java)
            // Clear error
            failureState.eventSink.invoke(ChangeServerEvents.ClearError)
            val finalState = awaitItem()
            assertThat(finalState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - change server unsupported server`() = runTest {
        val authenticationService = FakePRISMAuthenticationService(
            setHomeserverResult = {
                Result.success(aPRISMHomeServerDetails())
            },
        )
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(failureState.changeServerAction).isInstanceOf(AsyncData.Failure::class.java)
            assertThat(failureState.changeServerAction.errorOrNull()).isEqualTo(
                ChangeServerError.UnsupportedServer
            )
        }
    }

    @Test
    fun `present - change server not allowed error`() = runTest {
        val isAllowedToConnectToHomeserverResult = lambdaRecorder<String, Boolean> { false }
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = isAllowedToConnectToHomeserverResult,
                defaultHomeserverListResult = { listOf("prism.io") },
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            val anAccountProvider = AccountProvider(url = A_HOMESERVER_URL)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(anAccountProvider))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.UnauthorizedAccountProvider).unauthorisedAccountProviderTitle
            ).isEqualTo(anAccountProvider.title)
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.UnauthorizedAccountProvider).authorisedAccountProviderTitles
            ).containsExactly("prism.io")
            isAllowedToConnectToHomeserverResult.assertions()
                .isCalledOnce()
                .with(value(A_HOMESERVER_URL))
        }
    }

    @Test
    fun `present - change server prism pro required error`() = runTest {
        val getElementWellKnownResult = lambdaRecorder<String, WellknownRetrieverResult<PRISMWellKnown>> {
            WellknownRetrieverResult.Success(
                anPRISMWellKnown(
                    enforcePRISMPro = true,
                )
            )
        }
        createPresenter(
            wellknownRetriever = FakeWellknownRetriever(
                getElementWellKnownResult = getElementWellKnownResult,
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            val anAccountProvider = AccountProvider(url = A_HOMESERVER_URL)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(anAccountProvider))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.NeedPRISMPro).unauthorisedAccountProviderTitle
            ).isEqualTo(anAccountProvider.title)
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.NeedPRISMPro).applicationId
            ).isEqualTo("io.prism.enterprise")
            getElementWellKnownResult.assertions()
                .isCalledOnce()
                .with(value(A_HOMESERVER_URL.ensureProtocol()))
        }
    }

    private fun createPresenter(
        authenticationService: FakePRISMAuthenticationService = FakePRISMAuthenticationService(),
        accountProviderDataSource: AccountProviderDataSource = AccountProviderDataSource(FakeEnterpriseService()),
        enterpriseService: EnterpriseService = FakeEnterpriseService(),
        wellknownRetriever: WellknownRetriever = FakeWellknownRetriever(),
    ) = ChangeServerPresenter(
        authenticationService = authenticationService,
        accountProviderDataSource = accountProviderDataSource,
        defaultAccountProviderAccessControl = DefaultAccountProviderAccessControl(
            enterpriseService = enterpriseService,
            wellknownRetriever = wellknownRetriever,
        ),
    )
}
