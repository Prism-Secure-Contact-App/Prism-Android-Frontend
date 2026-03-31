/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl.direct

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.logout.api.direct.DirectLogoutEvents
import io.prism.android.features.logout.api.direct.DirectLogoutState
import io.prism.android.features.logout.api.direct.aDirectLogoutState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.pressBackKey
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultDirectLogoutViewTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on confirm logout sends expected Event`() {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        rule.setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_signout)
        eventsRecorder.assertSingle(DirectLogoutEvents.Logout(false))
    }

    @Test
    fun `clicking on cancel logout sends expected Event`() {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        rule.setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }

    @Ignore("Pressing back key should dismiss the dialog, and so generate the expected event, but it's not the case.")
    @Test
    fun `clicking on back invoke back callback`() {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        rule.setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        rule.pressBackKey()
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }

    @Test
    fun `clicking on confirm after error sends expected Event`() {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        rule.setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_signout_anyway)
        eventsRecorder.assertSingle(DirectLogoutEvents.Logout(true))
    }

    @Test
    fun `clicking on cancel after error sends expected Event`() {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        rule.setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setDefaultDirectLogoutView(
    state: DirectLogoutState,
) {
    setContent {
        DefaultDirectLogoutView().Render(state)
    }
}
