/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.reportroom.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.pressBack
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportRoomViewTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on back invoke the expected callback`() {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setReportRoomView(
                aReportRoomState(
                    eventSink = eventsRecorder,
                ),
                onBackClick = it
            )
            rule.pressBack()
        }
    }

    @Test
    fun `clicking on report when enabled emits the expected event`() {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        rule.setReportRoomView(
            aReportRoomState(
                reason = "Spam",
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(CommonStrings.action_report)
        eventsRecorder.assertSingle(ReportRoomEvents.Report)
    }

    @Test
    fun `clicking on decline when disabled does not emit event`() {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>(expectEvents = false)
        rule.setReportRoomView(
            aReportRoomState(eventSink = eventsRecorder),
        )
        rule.clickOn(CommonStrings.action_report)
    }

    @Test
    fun `clicking on leave room option emits the expected event`() {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        rule.setReportRoomView(
            aReportRoomState(eventSink = eventsRecorder),
        )
        rule.clickOn(CommonStrings.action_leave_room)
        eventsRecorder.assertSingle(ReportRoomEvents.ToggleLeaveRoom)
    }

    @Test
    fun `typing text in the reason field emits the expected Event`() {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        rule.setReportRoomView(
            aReportRoomState(
                eventSink = eventsRecorder,
                reason = ""
            ),
        )
        rule.onNodeWithText("").performTextInput("Spam!")
        eventsRecorder.assertSingle(ReportRoomEvents.UpdateReason("Spam!"))
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setReportRoomView(
    state: ReportRoomState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        ReportRoomView(
            state = state,
            onBackClick = onBackClick,
        )
    }
}
