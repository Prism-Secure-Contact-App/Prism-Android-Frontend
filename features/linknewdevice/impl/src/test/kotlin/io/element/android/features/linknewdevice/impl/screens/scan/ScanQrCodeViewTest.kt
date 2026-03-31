/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.scan

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.pressBackKey
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScanQrCodeViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `on back pressed - calls the expected callback`() {
        val eventRecorder = EventsRecorder<ScanQrCodeEvent>(expectEvents = false)
        ensureCalledOnce { callback ->
            rule.setView(
                state = aScanQrCodeState(
                    eventSink = eventRecorder,
                ),
                onBackClick = callback
            )
            rule.pressBackKey()
        }
    }

    @Test
    fun `try again button clicked - emits the expected event`() {
        val eventRecorder = EventsRecorder<ScanQrCodeEvent>()
        rule.setView(
            state = aScanQrCodeState(
                scanAction = AsyncAction.Failure(AN_EXCEPTION),
                eventSink = eventRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_try_again)
        eventRecorder.assertSingle(ScanQrCodeEvent.TryAgain)
    }

    private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setView(
        state: ScanQrCodeState = aScanQrCodeState(),
        onBackClick: () -> Unit = EnsureNeverCalled(),
    ) {
        setContent {
            ScanQrCodeView(
                state = state,
                onBackClick = onBackClick,
            )
        }
    }
}
