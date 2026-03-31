/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.forward.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.test.AN_EXCEPTION
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.testtags.TestTags
import io.prism.android.tests.testutils.EnsureNeverCalledWithParam
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.ensureCalledOnceWithParam
import io.prism.android.tests.testutils.pressTag
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForwardMessagesViewTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `cancel error emits the expected event`() {
        val eventsRecorder = EventsRecorder<ForwardMessagesEvents>()
        rule.setForwardMessagesView(
            aForwardMessagesState(
                forwardAction = AsyncAction.Failure(AN_EXCEPTION),
                eventSink = eventsRecorder
            ),
        )
        rule.pressTag(TestTags.dialogPositive.value)
        eventsRecorder.assertSingle(ForwardMessagesEvents.ClearError)
    }

    @Test
    fun `success invokes onForwardSuccess`() {
        val data = listOf(A_ROOM_ID)
        val eventsRecorder = EventsRecorder<ForwardMessagesEvents>(expectEvents = false)
        ensureCalledOnceWithParam<List<RoomId>?>(data) { callback ->
            rule.setForwardMessagesView(
                aForwardMessagesState(
                    forwardAction = AsyncAction.Success(data),
                    eventSink = eventsRecorder
                ),
                onForwardSuccess = callback,
            )
        }
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setForwardMessagesView(
    state: ForwardMessagesState,
    onForwardSuccess: (List<RoomId>) -> Unit = EnsureNeverCalledWithParam(),
) {
    setContent {
        ForwardMessagesView(
            state = state,
            onForwardSuccess = onForwardSuccess,
        )
    }
}
