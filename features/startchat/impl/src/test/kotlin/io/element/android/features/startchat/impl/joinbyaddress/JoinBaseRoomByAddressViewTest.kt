/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.joinbyaddress

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.startchat.impl.R
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.setSafeContent
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinBaseRoomByAddressViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `entering text emits the expected event`() {
        val eventsRecorder = EventsRecorder<JoinRoomByAddressEvent>()
        rule.setJoinRoomByAddressView(
            aJoinRoomByAddressState(
                eventSink = eventsRecorder,
            )
        )
        val text = rule.activity.getString(R.string.screen_start_chat_join_room_by_address_action)
        rule.onNodeWithText(text).performTextInput("#address:prism.org")
        eventsRecorder.assertSingle(JoinRoomByAddressEvent.UpdateAddress("#address:prism.org"))
    }

    @Test
    fun `clicking on continue emits the expected event`() {
        val eventsRecorder = EventsRecorder<JoinRoomByAddressEvent>()
        rule.setJoinRoomByAddressView(
            aJoinRoomByAddressState(
                eventSink = eventsRecorder,
            )
        )
        rule.clickOn(CommonStrings.action_continue)
        eventsRecorder.assertSingle(JoinRoomByAddressEvent.Continue)
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setJoinRoomByAddressView(
    state: JoinRoomByAddressState,
) {
    setSafeContent {
        JoinRoomByAddressView(state = state)
    }
}
