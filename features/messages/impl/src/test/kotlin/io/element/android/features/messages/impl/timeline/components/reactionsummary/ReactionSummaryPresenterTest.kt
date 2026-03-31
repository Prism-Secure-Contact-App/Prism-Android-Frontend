/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.reactionsummary

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.messages.impl.timeline.model.anAggregatedReaction
import io.prism.android.libraries.prism.api.room.RoomMembersState
import io.prism.android.libraries.prism.test.AN_AVATAR_URL
import io.prism.android.libraries.prism.test.AN_EVENT_ID
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.A_USER_NAME
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.aRoomMember
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ReactionSummaryPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    private val aggregatedReaction = anAggregatedReaction(userId = A_USER_ID, key = "👍", isHighlighted = true)
    private val roomMember = aRoomMember(userId = A_USER_ID, avatarUrl = AN_AVATAR_URL, displayName = A_USER_NAME)
    private val summaryEvent = ReactionSummaryEvent.ShowReactionSummary(AN_EVENT_ID, listOf(aggregatedReaction), aggregatedReaction.key)
    private val room = FakeBaseRoom().apply {
        givenRoomMembersState(RoomMembersState.Ready(persistentListOf(roomMember)))
    }
    private val presenter = ReactionSummaryPresenter(room)

    @Test
    fun `present - handle showing and hiding the reaction summary`() = runTest {
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.target).isNull()

            initialState.eventSink(summaryEvent)
            assertThat(awaitItem().target).isNotNull()

            initialState.eventSink(ReactionSummaryEvent.Clear)
            assertThat(awaitItem().target).isNull()
        }
    }

    @Test
    fun `present - handle reaction summary content and avatars populated`() = runTest {
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.target).isNull()

            initialState.eventSink(summaryEvent)
            val reactions = awaitItem().target?.reactions
            assertThat(reactions?.count()).isEqualTo(1)
            assertThat(reactions?.first()?.key).isEqualTo("👍")
            assertThat(reactions?.first()?.senders?.first()?.senderId).isEqualTo(A_USER_ID)
            assertThat(reactions?.first()?.senders?.first()?.user?.userId).isEqualTo(A_USER_ID)
            assertThat(reactions?.first()?.senders?.first()?.user?.avatarUrl).isEqualTo(AN_AVATAR_URL)
            assertThat(reactions?.first()?.senders?.first()?.user?.displayName).isEqualTo(A_USER_NAME)
        }
    }
}
