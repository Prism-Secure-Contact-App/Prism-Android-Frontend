/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomaliasresolver.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.prism.api.room.alias.ResolvedRoomAlias
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
class RoomAliasHelperViewTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on back invokes the expected callback`() {
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>(expectEvents = false)
        ensureCalledOnce {
            rule.setRoomAliasResolverView(
                aRoomAliasResolverState(
                    eventSink = eventsRecorder,
                ),
                onBackClick = it
            )
            rule.pressBack()
        }
    }

    @Test
    fun `clicking on Retry emits the expected Event`() {
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>()
        rule.setRoomAliasResolverView(
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            ),
        )
        rule.clickOn(CommonStrings.action_retry)
        eventsRecorder.assertSingle(RoomAliasResolverEvents.Retry)
    }

    @Test
    fun `success state invokes the expected Callback`() {
        val result = aResolvedRoomAlias()
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>(expectEvents = false)
        ensureCalledOnceWithParam(result) {
            rule.setRoomAliasResolverView(
                aRoomAliasResolverState(
                    resolveState = AsyncData.Success(result),
                    eventSink = eventsRecorder,
                ),
                onAliasResolved = it,
            )
        }
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setRoomAliasResolverView(
    state: RoomAliasResolverState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
    onAliasResolved: (ResolvedRoomAlias) -> Unit = EnsureNeverCalledWithParam(),
) {
    setContent {
        RoomAliasResolverView(
            state = state,
            onBackClick = onBackClick,
            onSuccess = onAliasResolved,
        )
    }
}
