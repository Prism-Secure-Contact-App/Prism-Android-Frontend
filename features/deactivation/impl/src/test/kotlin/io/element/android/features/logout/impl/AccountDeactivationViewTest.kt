/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.logout.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.deactivation.impl.R
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_PASSWORD
import io.prism.android.libraries.testtags.TestTags
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.pressBack
import io.prism.android.tests.testutils.pressTag
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class AccountDeactivationViewTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on back invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setAccountDeactivationView(
                state = anAccountDeactivationState(eventSink = eventsRecorder),
                onBackClick = it,
            )
            rule.pressBack()
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on Deactivate emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                deactivateFormState = aDeactivateFormState(
                    password = A_PASSWORD,
                ),
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(CommonStrings.action_deactivate)
        eventsRecorder.assertSingle(AccountDeactivationEvents.DeactivateAccount(false))
    }

    @Test
    fun `clicking on Deactivate on the confirmation dialog emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                deactivateFormState = aDeactivateFormState(
                    password = A_PASSWORD,
                ),
                accountDeactivationAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            ),
        )
        rule.pressTag(TestTags.dialogPositive.value)
        eventsRecorder.assertSingle(AccountDeactivationEvents.DeactivateAccount(false))
    }

    @Test
    fun `clicking on retry on the confirmation dialog emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                deactivateFormState = aDeactivateFormState(
                    password = A_PASSWORD,
                ),
                accountDeactivationAction = AsyncAction.Failure(AN_EXCEPTION),
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(CommonStrings.action_retry)
        eventsRecorder.assertSingle(AccountDeactivationEvents.DeactivateAccount(true))
    }

    @Test
    fun `switching on the erase all switch emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(R.string.screen_deactivate_account_delete_all_messages)
        eventsRecorder.assertSingle(AccountDeactivationEvents.SetEraseData(true))
    }

    @Test
    fun `switching off the erase all switch emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                deactivateFormState = aDeactivateFormState(
                    eraseData = true,
                ),
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(R.string.screen_deactivate_account_delete_all_messages)
        eventsRecorder.assertSingle(AccountDeactivationEvents.SetEraseData(false))
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `typing text in the password field emits the expected Event`() {
        val eventsRecorder = EventsRecorder<AccountDeactivationEvents>()
        rule.setAccountDeactivationView(
            state = anAccountDeactivationState(
                deactivateFormState = aDeactivateFormState(
                    password = A_PASSWORD,
                ),
                eventSink = eventsRecorder,
            ),
        )
        rule.onNodeWithTag(TestTags.loginPassword.value).performTextInput("A")
        eventsRecorder.assertSingle(AccountDeactivationEvents.SetPassword("A$A_PASSWORD"))
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setAccountDeactivationView(
    state: AccountDeactivationState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        AccountDeactivationView(
            state = state,
            onBackClick = onBackClick,
        )
    }
}
