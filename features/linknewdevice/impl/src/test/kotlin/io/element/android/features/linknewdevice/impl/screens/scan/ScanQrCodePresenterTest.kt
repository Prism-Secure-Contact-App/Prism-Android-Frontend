/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.prism.android.features.linknewdevice.impl.screens.scan

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.linknewdevice.impl.LinkNewDesktopHandler
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrCodeDecodeException
import io.prism.android.libraries.matrix.api.linknewdevice.LinkDesktopStep
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.QR_CODE_DATA_RECIPROCATE
import io.prism.android.libraries.matrix.test.linknewdevice.FakeLinkDesktopHandler
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ScanQrCodePresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val matrixClient = FakePRISMClient(
            createLinkDesktopHandlerResult = { Result.success(FakeLinkDesktopHandler()) }
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.scanAction.isLoading()).isTrue()
        }
    }

    @Test
    fun `present - handle scanned event - success`() = runTest {
        val handleScannedQrCodeResult = lambdaRecorder<ByteArray, Unit> { }
        val matrixClient = FakePRISMClient(
            sessionCoroutineScope = backgroundScope,
            createLinkDesktopHandlerResult = {
                Result.success(
                    FakeLinkDesktopHandler(
                        handleScannedQrCodeResult = handleScannedQrCodeResult,
                    )
                )
            }
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.scanAction.isLoading()).isTrue()
            initialState.eventSink(ScanQrCodeEvent.QrCodeScanned(QR_CODE_DATA_RECIPROCATE))
            val scannedState = awaitItem()
            assertThat(scannedState.scanAction.isSuccess()).isTrue()
            runCurrent()
            handleScannedQrCodeResult.assertions().isCalledOnce().with(value(QR_CODE_DATA_RECIPROCATE))
        }
    }

    @Test
    fun `present - handle scanned event - failure`() = runTest {
        val handleScannedQrCodeResult = lambdaRecorder<ByteArray, Unit> { }
        val handler = FakeLinkDesktopHandler(
            handleScannedQrCodeResult = handleScannedQrCodeResult,
        )
        val matrixClient = FakePRISMClient(
            sessionCoroutineScope = backgroundScope,
            createLinkDesktopHandlerResult = {
                Result.success(handler)
            }
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.scanAction.isLoading()).isTrue()
            initialState.eventSink(ScanQrCodeEvent.QrCodeScanned(QR_CODE_DATA_RECIPROCATE))
            val scannedState = awaitItem()
            assertThat(scannedState.scanAction.isSuccess()).isTrue()
            handler.emitStep(LinkDesktopStep.InvalidQrCode(QrCodeDecodeException.Crypto("Invalid QR Code")))
            skipItems(1)
            val errorState = awaitItem()
            assertThat(errorState.scanAction.isFailure()).isTrue()
            handleScannedQrCodeResult.assertions().isCalledOnce().with(value(QR_CODE_DATA_RECIPROCATE))
            // Reset by trying again
            errorState.eventSink(ScanQrCodeEvent.TryAgain)
            val resetState = awaitItem()
            assertThat(resetState.scanAction.isLoading()).isTrue()
        }
    }
}

private fun createPresenter(
    matrixClient: PRISMClient,
) = ScanQrCodePresenter(
    linkNewDesktopHandler = LinkNewDesktopHandler(matrixClient),
)
