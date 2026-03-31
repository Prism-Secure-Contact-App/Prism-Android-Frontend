/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.desktop

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.linknewdevice.impl.R
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.pressBack
import io.prism.android.tests.testutils.pressBackKey
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DesktopNoticeViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `on back pressed - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setView(
                state = aDesktopNoticeState(),
                onBackClicked = callback,
            )
            rule.pressBackKey()
        }
    }

    @Test
    fun `on back button clicked - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setView(
                state = aDesktopNoticeState(),
                onBackClicked = callback,
            )
            rule.pressBack()
        }
    }

    @Test
    fun `when can continue - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setView(
                state = aDesktopNoticeState(canContinue = true),
                onReadyToScanClick = callback,
            )
        }
    }

    @Test
    fun `on submit button clicked - emits the Continue event`() {
        val eventRecorder = EventsRecorder<DesktopNoticeEvent>()
        rule.setView(
            state = aDesktopNoticeState(eventSink = eventRecorder),
        )
        rule.clickOn(R.string.screen_link_new_device_desktop_submit)
        eventRecorder.assertSingle(DesktopNoticeEvent.Continue)
    }

    private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setView(
        state: DesktopNoticeState,
        onBackClicked: () -> Unit = EnsureNeverCalled(),
        onReadyToScanClick: () -> Unit = EnsureNeverCalled(),
    ) {
        setContent {
            DesktopNoticeView(
                state = state,
                onBackClick = onBackClicked,
                onReadyToScanClick = onReadyToScanClick,
            )
        }
    }
}
