/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.details

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.ui.strings.CommonStrings
import io.prism.android.tests.testutils.EnsureNeverCalled
import io.prism.android.tests.testutils.EnsureNeverCalledWithParam
import io.prism.android.tests.testutils.clickOn
import io.prism.android.tests.testutils.ensureCalledOnce
import io.prism.android.tests.testutils.ensureCalledOnceWithParam
import io.prism.android.tests.testutils.setSafeContent
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDeleteConfirmationBottomSheetTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `clicking on Cancel invokes expected callback`() {
        val state = aMediaDeleteConfirmationState()
        ensureCalledOnce { callback ->
            rule.setMediaDeleteConfirmationBottomSheet(
                state = state,
                onDismiss = callback,
            )
            rule.clickOn(CommonStrings.action_cancel)
        }
    }

    @Test
    fun `clicking on Remove invokes expected callback`() {
        val state = aMediaDeleteConfirmationState()
        ensureCalledOnceWithParam(state.eventId) { callback ->
            rule.setMediaDeleteConfirmationBottomSheet(
                state = state,
                onDelete = callback,
            )
            rule.onNodeWithText(rule.activity.getString(CommonStrings.action_remove)).assertExists()
            rule.clickOn(CommonStrings.action_remove)
        }
    }
}

private fun <R : TestRule> AndroidComposeTestRule<R, ComponentActivity>.setMediaDeleteConfirmationBottomSheet(
    state: MediaBottomSheetState.MediaDeleteConfirmationState,
    onDelete: (EventId) -> Unit = EnsureNeverCalledWithParam(),
    onDismiss: () -> Unit = EnsureNeverCalled(),
) {
    setSafeContent {
        MediaDeleteConfirmationBottomSheet(
            state = state,
            onDelete = onDelete,
            onDismiss = onDismiss,
        )
    }
}
