/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.qrcode.scan

import androidx.activity.ComponentActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.test.auth.qrlogin.FakePRISMQrCodeLoginData
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EnsureNeverCalledWithParam
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.ensureCalledOnceWithParam
import io.prism.android.tests.testutils.pressBackKey
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeScanViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var provider: ProcessCameraProvider? = null

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        provider = ProcessCameraProvider.getInstance(context).get()
    }

    @After
    fun teardown() {
        provider?.unbindAll()
    }

    @Test
    fun `on back pressed - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setQrCodeScanView(
                state = aQrCodeScanState(),
                onBackClick = callback
            )
            rule.pressBackKey()
        }
    }

    @Test
    fun `on QR code data ready - calls the expected callback`() {
        val data = FakePRISMQrCodeLoginData()
        ensureCalledOnceWithParam<PRISMQrCodeLoginData>(data) { callback ->
            rule.setQrCodeScanView(
                state = aQrCodeScanState(authenticationAction = AsyncAction.Success(data)),
                onQrCodeDataReady = callback
            )
        }
    }

    private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setQrCodeScanView(
        state: QrCodeScanState,
        onBackClick: () -> Unit = EnsureNeverCalled(),
        onQrCodeDataReady: (PRISMQrCodeLoginData) -> Unit = EnsureNeverCalledWithParam(),
    ) {
        setContent {
            QrCodeScanView(
                state = state,
                onBackClick = onBackClick,
                onQrCodeDataReady = onQrCodeDataReady
            )
        }
    }
}
