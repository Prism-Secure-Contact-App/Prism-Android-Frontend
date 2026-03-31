/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.encryption.BackupState
import io.prism.android.libraries.prism.api.encryption.BackupUploadState
import io.prism.android.libraries.prism.api.encryption.EncryptionService
import io.prism.android.libraries.prism.api.encryption.RecoveryState
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.encryption.FakeEncryptionService
import io.prism.android.libraries.workmanager.api.WorkManagerRequestType
import io.prism.android.libraries.workmanager.test.FakeWorkManagerScheduler
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LogoutPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createLogoutPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            assertThat(initialState.isLastDevice).isFalse()
            assertThat(initialState.backupState).isEqualTo(BackupState.UNKNOWN)
            assertThat(initialState.doesBackupExistOnServer).isTrue()
            assertThat(initialState.recoveryState).isEqualTo(RecoveryState.UNKNOWN)
            assertThat(initialState.backupUploadState).isEqualTo(BackupUploadState.Unknown)
            assertThat(initialState.waitingForALongTime).isFalse()
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - initial state - last session`() = runTest {
        val presenter = createLogoutPresenter(
            encryptionService = FakeEncryptionService().apply {
                emitIsLastDevice(true)
            }
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(2)
            val initialState = awaitItem()
            assertThat(initialState.isLastDevice).isTrue()
            assertThat(initialState.backupUploadState).isEqualTo(BackupUploadState.Unknown)
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - initial state - waiting a long time`() = runTest {
        val encryptionService = FakeEncryptionService()
        encryptionService.givenWaitForBackupUploadSteadyStateFlow(
            flow {
                emit(BackupUploadState.Waiting)
                delay(3_000)
            }
        )
        val presenter = createLogoutPresenter(
            encryptionService = encryptionService
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.waitingForALongTime).isFalse()
            assertThat(initialState.backupUploadState).isEqualTo(BackupUploadState.Unknown)
            val waitingState = awaitItem()
            assertThat(waitingState.backupUploadState).isEqualTo(BackupUploadState.Waiting)
            assertThat(initialState.waitingForALongTime).isFalse()
            skipItems(1)
            val waitingALongTimeState = awaitItem()
            assertThat(waitingALongTimeState.backupUploadState).isEqualTo(BackupUploadState.Waiting)
            assertThat(waitingALongTimeState.waitingForALongTime).isTrue()
        }
    }

    @Test
    fun `present - initial state - backing up`() = runTest {
        val encryptionService = FakeEncryptionService()
        encryptionService.givenWaitForBackupUploadSteadyStateFlow(
            flow {
                emit(BackupUploadState.Waiting)
                delay(1)
                emit(BackupUploadState.Uploading(backedUpCount = 1, totalCount = 2))
                delay(1)
                emit(BackupUploadState.Done)
            }
        )
        val presenter = createLogoutPresenter(
            encryptionService = encryptionService
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.isLastDevice).isFalse()
            assertThat(initialState.backupUploadState).isEqualTo(BackupUploadState.Unknown)
            assertThat(initialState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
            val waitingState = awaitItem()
            assertThat(waitingState.backupUploadState).isEqualTo(BackupUploadState.Waiting)
            skipItems(1)
            val uploadingState = awaitItem()
            assertThat(uploadingState.backupUploadState).isEqualTo(BackupUploadState.Uploading(backedUpCount = 1, totalCount = 2))
            val doneState = awaitItem()
            assertThat(doneState.backupUploadState).isEqualTo(BackupUploadState.Done)
        }
    }

    @Test
    fun `present - logout then cancel`() = runTest {
        val presenter = createLogoutPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            initialState.eventSink.invoke(LogoutEvents.CloseDialogs)
            val finalState = awaitItem()
            assertThat(finalState.logoutAction).isEqualTo(AsyncAction.Uninitialized)
        }
    }

    @Test
    fun `present - logout then confirm`() = runTest {
        val cancelWorkManagerJobsLambda = lambdaRecorder<SessionId, WorkManagerRequestType?, Unit> { _, _ -> }
        val workManagerScheduler = FakeWorkManagerScheduler(cancelLambda = cancelWorkManagerJobsLambda)
        val presenter = createLogoutPresenter(workManagerScheduler = workManagerScheduler)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.logoutAction).isInstanceOf(AsyncAction.Success::class.java)

            cancelWorkManagerJobsLambda.assertions().isCalledOnce()
        }
    }

    @Test
    fun `present - logout with error then cancel`() = runTest {
        val prismClient = FakePRISMClient().apply {
            logoutLambda = { _, _ ->
                throw AN_EXCEPTION
            }
        }
        val presenter = createLogoutPresenter(
            prismClient,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState.logoutAction).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
            errorState.eventSink.invoke(LogoutEvents.CloseDialogs)
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
        val presenter = createLogoutPresenter(
            prismClient,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitFirstItem()
            initialState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val confirmationState = awaitItem()
            assertThat(confirmationState.logoutAction).isEqualTo(AsyncAction.ConfirmingNoParams)
            confirmationState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = false))
            val loadingState = awaitItem()
            assertThat(loadingState.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState.logoutAction).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
            errorState.eventSink.invoke(LogoutEvents.Logout(ignoreSdkError = true))
            val loadingState2 = awaitItem()
            assertThat(loadingState2.logoutAction).isInstanceOf(AsyncAction.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.logoutAction).isInstanceOf(AsyncAction.Success::class.java)
        }
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitFirstItem(): T {
        skipItems(2)
        return awaitItem()
    }
}

internal fun createLogoutPresenter(
    prismClient: PRISMClient = FakePRISMClient(),
    encryptionService: EncryptionService = FakeEncryptionService(),
    workManagerScheduler: FakeWorkManagerScheduler = FakeWorkManagerScheduler(cancelLambda = { _, _ -> }),
): LogoutPresenter = LogoutPresenter(
    prismClient = prismClient,
    encryptionService = encryptionService,
    workManagerScheduler = workManagerScheduler,
)
