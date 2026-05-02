/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.root

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.linknewdevice.impl.LinkNewMobileHandler
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.test.AN_EXCEPTION
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.linknewdevice.FakeLinkMobileHandler
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LinkNewDeviceRootPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val matrixClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(true) },
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.isSupported.isUninitialized()).isTrue()
            assertThat(awaitItem().isSupported.dataOrNull()).isTrue()
        }
    }

    @Test
    fun `present - new login device not supported`() = runTest {
        val matrixClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(false) },
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.isSupported.isUninitialized()).isTrue()
            assertThat(awaitItem().isSupported.dataOrNull()).isFalse()
        }
    }

    @Test
    fun `present - error`() = runTest {
        val matrixClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.failure(AN_EXCEPTION) },
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.isSupported.isUninitialized()).isTrue()
            assertThat(awaitItem().isSupported.isFailure()).isTrue()
        }
    }

    @Test
    fun `present - link new mobile device`() = runTest {
        val linkMobileHandler = FakeLinkMobileHandler(
            startResult = {},
        )
        val matrixClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(true) },
            sessionCoroutineScope = backgroundScope,
            createLinkMobileHandlerResult = { Result.success(linkMobileHandler) }
        )
        createPresenter(
            matrixClient = matrixClient,
        ).test {
            skipItems(1)
            val initialState = awaitItem()
            assertThat(initialState.isSupported.dataOrNull()).isTrue()
            initialState.eventSink(LinkNewDeviceRootEvent.LinkMobileDevice)
            val loadingState = awaitItem()
            assertThat(loadingState.qrCodeData.isLoading()).isTrue()
        }
    }

    private fun createPresenter(
        matrixClient: PRISMClient = FakePRISMClient(),
        linkNewMobileHandler: LinkNewMobileHandler = LinkNewMobileHandler(matrixClient),
    ) = LinkNewDeviceRootPresenter(
        matrixClient = matrixClient,
        linkNewMobileHandler = linkNewMobileHandler,
    )
}
