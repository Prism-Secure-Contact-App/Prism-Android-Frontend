/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.login.impl.R
import io.prism.android.features.login.impl.login.LoginMode
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.matrix.api.auth.OidcDetails
import io.prism.android.libraries.matrix.test.AN_EXCEPTION
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EnsureNeverCalledWithParam
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.ensureCalledOnceWithParam
import io.prism.android.tests.testutils.pressBack
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `when can create account - clicking on create account calls the expected callback`() {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    canCreateAccount = true,
                    eventSink = eventSink,
                ),
                onCreateAccount = callback,
            )
            rule.clickOn(R.string.screen_onboarding_sign_up)
        }
    }

    @Test
    fun `when can go back - clicking on back calls the expected callback`() {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    isAddingAccount = true,
                    eventSink = eventSink,
                ),
                onBackClick = callback,
            )
            rule.pressBack()
        }
    }

    @Test
    fun `when can login with QR code - clicking on sign in with QR code calls the expected callback`() {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    canLoginWithQrCode = true,
                    eventSink = eventSink,
                ),
                onSignInWithQrCode = callback,
            )
            rule.clickOn(R.string.screen_onboarding_sign_in_with_qr_code)
        }
    }

    @Test
    fun `when can login with QR code - clicking on sign in manually calls the expected callback - can search account provider`() {
        `when can login with QR code - clicking on sign in manually calls the expected callback`(
            mustChooseAccountProvider = false,
        )
    }

    @Test
    fun `when can login with QR code - clicking on sign in manually calls the expected callback - cannot search account provider`() {
        `when can login with QR code - clicking on sign in manually calls the expected callback`(
            mustChooseAccountProvider = true,
        )
    }

    private fun `when can login with QR code - clicking on sign in manually calls the expected callback`(
        mustChooseAccountProvider: Boolean,
    ) {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnceWithParam(mustChooseAccountProvider) { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    canLoginWithQrCode = true,
                    mustChooseAccountProvider = mustChooseAccountProvider,
                    eventSink = eventSink,
                ),
                onSignIn = callback,
            )
            rule.clickOn(R.string.screen_onboarding_sign_in_manually)
        }
    }

    @Test
    fun `when cannot login with QR code or create account - clicking on continue calls the sign in callback - can search account provider`() {
        `when cannot login with QR code or create account - clicking on continue calls the sign in callback`(
            mustChooseAccountProvider = false,
        )
    }

    @Test
    fun `when cannot login with QR code or create account - clicking on continue calls the sign in callback - cannot search account provider`() {
        `when cannot login with QR code or create account - clicking on continue calls the sign in callback`(
            mustChooseAccountProvider = true,
        )
    }

    private fun `when cannot login with QR code or create account - clicking on continue calls the sign in callback`(
        mustChooseAccountProvider: Boolean,
    ) {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnceWithParam(mustChooseAccountProvider) { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    canLoginWithQrCode = false,
                    canCreateAccount = false,
                    mustChooseAccountProvider = mustChooseAccountProvider,
                    eventSink = eventSink,
                ),
                onSignIn = callback,
            )
            rule.clickOn(CommonStrings.action_continue)
        }
    }

    @Test
    fun `when sign in to pre defined account provider - clicking on button emits the expected event`() {
        val eventSink = EventsRecorder<OnBoardingEvents>()
        rule.setOnboardingView(
            state = anOnBoardingState(
                defaultAccountProvider = "prism.io",
                eventSink = eventSink,
            ),
        )
        val buttonText = rule.activity.getString(R.string.screen_onboarding_sign_in_to, "prism.io")
        rule.onNodeWithText(buttonText).performClick()
        eventSink.assertSingle(OnBoardingEvents.OnSignIn("prism.io"))
    }

    @Test
    fun `when error is displayed - closing the dialog emits the expected event`() {
        val eventSink = EventsRecorder<OnBoardingEvents>()
        rule.setOnboardingView(
            state = anOnBoardingState(
                defaultAccountProvider = "prism.io",
                loginMode = AsyncData.Failure(AN_EXCEPTION),
                eventSink = eventSink,
            ),
        )
        rule.clickOn(CommonStrings.action_ok)
        eventSink.assertSingle(OnBoardingEvents.ClearError)
    }

    @Test
    fun `clicking on report a problem calls the sign in callback`() {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    canReportBug = true,
                    eventSink = eventSink,
                ),
                onReportProblem = callback,
            )
            val text = rule.activity.getString(CommonStrings.common_report_a_problem)
            rule.onNodeWithText(text).assertExists()
            rule.clickOn(CommonStrings.common_report_a_problem)
        }
    }

    @Test
    fun `cannot report a problem when the feature is disabled`() {
        val eventSink = EventsRecorder<OnBoardingEvents>(expectEvents = false)
        rule.setOnboardingView(
            state = anOnBoardingState(
                canReportBug = false,
                eventSink = eventSink,
            ),
        )
        val text = rule.activity.getString(CommonStrings.common_report_a_problem)
        rule.onNodeWithText(text).assertDoesNotExist()
    }

    @Test
    fun `when success PasswordLogin - the expected callback is invoked and the event is received`() {
        val eventSink = EventsRecorder<OnBoardingEvents>()
        ensureCalledOnce { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    loginMode = AsyncData.Success(LoginMode.PasswordLogin),
                    eventSink = eventSink,
                ),
                onNeedLoginPassword = callback,
            )
        }
        eventSink.assertSingle(OnBoardingEvents.ClearError)
    }

    @Test
    fun `when success Oidc - the expected callback is invoked and the event is received`() {
        val eventSink = EventsRecorder<OnBoardingEvents>()
        val oidcDetails = OidcDetails("aUrl")
        ensureCalledOnceWithParam(oidcDetails) { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    loginMode = AsyncData.Success(LoginMode.Oidc(oidcDetails)),
                    eventSink = eventSink,
                ),
                onOidcDetails = callback,
            )
        }
        eventSink.assertSingle(OnBoardingEvents.ClearError)
    }

    @Test
    fun `when success AccountCreation - the expected callback is invoked and the event is received`() {
        val eventSink = EventsRecorder<OnBoardingEvents>()
        val oidcDetails = OidcDetails("aUrl")
        ensureCalledOnceWithParam(oidcDetails.url) { callback ->
            rule.setOnboardingView(
                state = anOnBoardingState(
                    loginMode = AsyncData.Success(LoginMode.AccountCreation("aUrl")),
                    eventSink = eventSink,
                ),
                onCreateAccountContinue = callback,
            )
        }
        eventSink.assertSingle(OnBoardingEvents.ClearError)
    }

    private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setOnboardingView(
        state: OnBoardingState,
        onBackClick: () -> Unit = EnsureNeverCalled(),
        onSignInWithQrCode: () -> Unit = EnsureNeverCalled(),
        onSignIn: (Boolean) -> Unit = EnsureNeverCalledWithParam(),
        onCreateAccount: () -> Unit = EnsureNeverCalled(),
        onReportProblem: () -> Unit = EnsureNeverCalled(),
        onOidcDetails: (OidcDetails) -> Unit = EnsureNeverCalledWithParam(),
        onNeedLoginPassword: () -> Unit = EnsureNeverCalled(),
        onLearnMoreClick: () -> Unit = EnsureNeverCalled(),
        onCreateAccountContinue: (url: String) -> Unit = EnsureNeverCalledWithParam(),
    ) {
        setContent {
            OnBoardingView(
                state = state,
                onBackClick = onBackClick,
                onSignInWithQrCode = onSignInWithQrCode,
                onSignIn = onSignIn,
                onCreateAccount = onCreateAccount,
                onReportProblem = onReportProblem,
                onOidcDetails = onOidcDetails,
                onNeedLoginPassword = onNeedLoginPassword,
                onLearnMoreClick = onLearnMoreClick,
                onCreateAccountContinue = onCreateAccountContinue,
            )
        }
    }
}
