/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.roomlist

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.features.home.impl.R
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureCalledOnceWithParam
import io.prism.android.tests.testutils.EnsureNeverCalledWithParam
import io.prism.android.tests.testutils.EventsRecorder
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.setSafeContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomListContextMenuTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on Mark as read generates expected Events`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown(hasNewContent = true)
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            eventSink = eventsRecorder,
        )
        rule.clickOn(R.string.screen_roomlist_mark_as_read)
        eventsRecorder.assertList(
            listOf(
                RoomListEvent.HideContextMenu,
                RoomListEvent.MarkAsRead(contextMenu.roomId),
            )
        )
    }

    @Test
    fun `clicking on Mark as unread generates expected Events`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown(hasNewContent = false)
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            eventSink = eventsRecorder,
        )
        rule.clickOn(R.string.screen_roomlist_mark_as_unread)
        eventsRecorder.assertList(
            listOf(
                RoomListEvent.HideContextMenu,
                RoomListEvent.MarkAsUnread(contextMenu.roomId),
            )
        )
    }

    @Test
    fun `clicking on Leave room generates expected Events`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown(isDm = false)
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            eventSink = eventsRecorder,
        )
        rule.clickOn(CommonStrings.action_leave_room)
        eventsRecorder.assertList(
            listOf(
                RoomListEvent.HideContextMenu,
                RoomListEvent.LeaveRoom(contextMenu.roomId, needsConfirmation = true),
            )
        )
    }

    @Test
    fun `clicking on Report room invokes the expected callback and generates expected Event`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown()
        val callback = EnsureCalledOnceWithParam(contextMenu.roomId, Unit)
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            canReportRoom = true,
            eventSink = eventsRecorder,
            onRoomSettingsClick = EnsureNeverCalledWithParam(),
            onReportRoomClick = callback,
        )
        rule.clickOn(CommonStrings.action_report_room)
        eventsRecorder.assertSingle(RoomListEvent.HideContextMenu)
        callback.assertSuccess()
    }

    @Test
    fun `clicking on Settings invokes the expected callback and generates expected Event`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown()
        val callback = EnsureCalledOnceWithParam(contextMenu.roomId, Unit)
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            eventSink = eventsRecorder,
            onRoomSettingsClick = callback,
        )
        rule.clickOn(CommonStrings.common_settings)
        eventsRecorder.assertSingle(RoomListEvent.HideContextMenu)
        callback.assertSuccess()
    }

    @Test
    fun `clicking on Favourites generates expected Event`() {
        val eventsRecorder = EventsRecorder<RoomListEvent>()
        val contextMenu = aContextMenuShown(isDm = false, isFavorite = false)
        val callback = EnsureNeverCalledWithParam<RoomId>()
        rule.setRoomListContextMenu(
            contextMenu = contextMenu,
            eventSink = eventsRecorder,
            onRoomSettingsClick = callback,
        )
        rule.clickOn(CommonStrings.common_favourite)
        eventsRecorder.assertList(
            listOf(
                RoomListEvent.SetRoomIsFavorite(contextMenu.roomId, true),
            )
        )
    }

    private fun AndroidComposeTestRule<*, *>.setRoomListContextMenu(
        contextMenu: RoomListState.ContextMenu.Shown,
        canReportRoom: Boolean = false,
        eventSink: (RoomListEvent) -> Unit,
        onRoomSettingsClick: (RoomId) -> Unit = EnsureNeverCalledWithParam(),
        onReportRoomClick: (RoomId) -> Unit = EnsureNeverCalledWithParam(),
    ) {
        setSafeContent {
            RoomListContextMenu(
                contextMenu = contextMenu,
                canReportRoom = canReportRoom,
                onRoomSettingsClick = onRoomSettingsClick,
                onReportRoomClick = onReportRoomClick,
                eventSink = eventSink,
            )
        }
    }
}
