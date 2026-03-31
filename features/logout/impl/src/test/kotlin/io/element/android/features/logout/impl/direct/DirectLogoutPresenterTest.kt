/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl.direct

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.logout.api.direct.DirectLogoutEvents
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.encryption.BackupUploadState
import io.prism.android.libraries.prism.api.encryption.EncryptionService
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.encryption.FakeEncryptionService
import io.prism.android.tests.testutils.WarmUpRule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DirectLogoutPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createDirectLogoutPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            assertThat(initialState.canDoDirectSignOut).isTrue()
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - initial state - last session`() = runTest {
        val presenter = createDirectLogoutPresenter(
            encryptionService = FakeEncryptionService().apply {
                emitIsLastDevice(true)
            }
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            assertThat(initialState.canDoDirectSignOut).isFalse()
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - initial state - backing up`() = runTest {
        val encryptionService = FakeEncryptionService()
        encryptionService.givenWaitForBackupUploadSteadyStateFlow(
            flow {
                emit(BackupUploadState.Waiting)
            }
        )
        val presenter = createDirectLogoutPresenter(
            encryptionService = encryptionService
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val initialState = awaitFirstItem()
            assertThat(initialState.canDoDirectSignOut).isFalse()
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - logout then cancel`() = runTest {
        val presenter = createDirectLogoutPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            initialState.eventSink.invoke(DirectLogoutEvents.CloseDialogs)
            val finalState = awaitItem()
            assertThat(finalState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - logout then confirm`() = runTest {
        val presenter = createDirectLogoutPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.logoutAction).isInstanceOf(AsyncAction.Success::class.java)
        }
    }

    @Test
    fun `present - logout with error then cancel`() = runTest {
        val prismClient = FakePRISMClient().apply {
            logoutLambda = { _, _ ->
                throw AN_EXCEPTION
            }
        }
        val presenter = createDirectLogoutPresenter(
            prismClient,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState.logoutAction).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
            errorState.eventSink.invoke(DirectLogoutEvents.CloseDialogs)
            val finalState = awaitItem()
            assertThat(finalState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - logout with error then force`() = runTest {
        val prismClient = FakePRISMClient().apply {
            logoutLambda = { ignoreSdkError, _ ->
                if (!ignoreSdkError) {
                    throw AN_EXCEPTION
                }
            }
        }
        val presenter = createDirectLogoutPresenter(
            prismClient,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState.logoutAction).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
            errorState.eventSink.invoke(DirectLogoutEvents.Logout(ignoreSdkError = true))
            val loadingState2 = awaitItem()
            assertThat(loadingState2.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.logoutAction).isInstanceOf(AsyncAction.Success::class.java)
        }
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitFirstItem(): T {
        return awaitItem()
    }

    private fun createDirectLogoutPresenter(
        prismClient: PRISMClient = FakePRISMClient(),
        encryptionService: EncryptionService = FakeEncryptionService(),
    ): DirectLogoutPresenter = DirectLogoutPresenter(
        prismClient = prismClient,
        encryptionService = encryptionService,
    )
}
