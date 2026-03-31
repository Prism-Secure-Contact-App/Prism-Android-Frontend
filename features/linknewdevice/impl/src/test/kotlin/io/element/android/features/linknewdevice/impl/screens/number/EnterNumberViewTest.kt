/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.number

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.ui.strings.CommonStrings
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
class EnterNumberViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `on back pressed - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setView(
                state = aEnterNumberState(),
                onBackClicked = callback,
            )
            rule.pressBackKey()
        }
    }

    @Test
    fun `on back button clicked - calls the expected callback`() {
        ensureCalledOnce { callback ->
            rule.setView(
                state = aEnterNumberState(),
                onBackClicked = callback,
            )
            rule.pressBack()
        }
    }

    @Test
    fun `on continue button clicked - emits the Continue event`() {
        val eventRecorder = EventsRecorder<EnterNumberEvent>()
        rule.setView(
            state = aEnterNumberState(
                number = "12",
                eventSink = eventRecorder,
            ),
        )
        rule.clickOn(CommonStrings.action_continue)
        eventRecorder.assertSingle(EnterNumberEvent.Continue)
    }

    @Test
    fun `when the number is not complete, continue button is disabled`() {
        val eventRecorder = EventsRecorder<EnterNumberEvent>(expectEvents = false)
        rule.setView(
            state = aEnterNumberState(
                number = "1",
                eventSink = eventRecorder,
            ),
        )
        val continueStr = rule.activity.getString(CommonStrings.action_continue)
        rule.onNodeWithText(continueStr).assertIsNotEnabled()
    }

    private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setView(
        state: EnterNumberState,
        onBackClicked: () -> Unit = EnsureNeverCalled(),
    ) {
        setContent {
            EnterNumberView(
                state = state,
                onBackClick = onBackClicked,
            )
        }
    }
}
