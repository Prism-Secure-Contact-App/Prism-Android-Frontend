/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.root

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.startchat.impl.R
import io.prism.android.features.startchat.impl.userlist.aRecentDirectRoomList
import io.prism.android.features.startchat.impl.userlist.aUserListState
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.ui.model.getBestName
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
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class StartChatViewTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on back invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setStartChatView(
                aCreateRoomRootState(
                    eventSink = eventsRecorder,
                ),
                onCloseClick = it
            )
            rule.pressBack()
        }
    }

    @Test
    fun `clicking on New room invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setStartChatView(
                aCreateRoomRootState(
                    eventSink = eventsRecorder,
                ),
                onNewRoomClick = it
            )
            rule.clickOn(R.string.screen_create_room_action_create_room)
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on Invite people invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setStartChatView(
                aCreateRoomRootState(
                    applicationName = "test",
                    eventSink = eventsRecorder,
                ),
                onInviteFriendsClick = it
            )
            val text = rule.activity.getString(CommonStrings.action_invite_friends_to_app, "test")
            rule.onNodeWithText(text).performClick()
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on a user suggestion invokes the expected callback`() {
        val recentDirectRoomList = aRecentDirectRoomList()
        val firstRoom = recentDirectRoomList[0]
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnceWithParam(firstRoom.roomId) {
            rule.setStartChatView(
                aCreateRoomRootState(
                    userListState = aUserListState(
                        recentDirectRooms = recentDirectRoomList
                    ),
                    eventSink = eventsRecorder,
                ),
                onOpenDM = it
            )
            rule.onNodeWithText(firstRoom.prismUser.getBestName()).performClick()
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on Join room by address invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setStartChatView(
                aCreateRoomRootState(
                    eventSink = eventsRecorder,
                ),
                onJoinRoomByAddressClick = it
            )
            rule.clickOn(R.string.screen_start_chat_join_room_by_address_action)
        }
    }

    @Test
    fun `clicking on room directory invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<StartChatEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setStartChatView(
                aCreateRoomRootState(
                    eventSink = eventsRecorder,
                    isRoomDirectorySearchEnabled = true
                ),
                onRoomDirectorySearchClick = it
            )
            rule.clickOn(R.string.screen_room_directory_search_title)
        }
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setStartChatView(
    state: StartChatState,
    onCloseClick: () -> Unit = EnsureNeverCalled(),
    onNewRoomClick: () -> Unit = EnsureNeverCalled(),
    onOpenDM: (RoomId) -> Unit = EnsureNeverCalledWithParam(),
    onInviteFriendsClick: () -> Unit = EnsureNeverCalled(),
    onJoinRoomByAddressClick: () -> Unit = EnsureNeverCalled(),
    onRoomDirectorySearchClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        StartChatView(
            state = state,
            onCloseClick = onCloseClick,
            onNewRoomClick = onNewRoomClick,
            onOpenDM = onOpenDM,
            onInviteFriendsClick = onInviteFriendsClick,
            onJoinByAddressClick = onJoinRoomByAddressClick,
            onRoomDirectorySearchClick = onRoomDirectorySearchClick,
        )
    }
}
