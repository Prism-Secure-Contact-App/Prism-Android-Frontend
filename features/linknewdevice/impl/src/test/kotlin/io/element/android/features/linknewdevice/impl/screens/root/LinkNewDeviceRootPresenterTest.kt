/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.root

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.linknewdevice.impl.LinkNewMobileHandler
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.test.linknewdevice.FakeLinkMobileHandler
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
        val prismClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(true) },
        )
        createPresenter(
            prismClient = prismClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.isSupported.isUninitialized()).isTrue()
            assertThat(awaitItem().isSupported.dataOrNull()).isTrue()
        }
    }

    @Test
    fun `present - new login device not supported`() = runTest {
        val prismClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(false) },
        )
        createPresenter(
            prismClient = prismClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.isSupported.isUninitialized()).isTrue()
            assertThat(awaitItem().isSupported.dataOrNull()).isFalse()
        }
    }

    @Test
    fun `present - error`() = runTest {
        val prismClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.failure(AN_EXCEPTION) },
        )
        createPresenter(
            prismClient = prismClient,
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
        val prismClient = FakePRISMClient(
            canLinkNewDeviceResult = { Result.success(true) },
            sessionCoroutineScope = backgroundScope,
            createLinkMobileHandlerResult = { Result.success(linkMobileHandler) }
        )
        createPresenter(
            prismClient = prismClient,
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
        prismClient: PRISMClient = FakePRISMClient(),
        linkNewMobileHandler: LinkNewMobileHandler = LinkNewMobileHandler(prismClient),
    ) = LinkNewDeviceRootPresenter(
        prismClient = prismClient,
        linkNewMobileHandler = linkNewMobileHandler,
    )
}
