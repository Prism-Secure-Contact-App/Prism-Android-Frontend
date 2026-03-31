/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet

import com.google.common.truth.Truth.assertThat
import io.prism.android.features.messages.impl.timeline.aTimelineItemEvent
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ReadReceiptBottomSheetPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - handle event selected`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            val selectedEvent = aTimelineItemEvent()
            initialState.eventSink(ReadReceiptBottomSheetEvent.EventSelected(selectedEvent))
            assertThat(awaitItem().selectedEvent).isSameInstanceAs(selectedEvent)
        }
    }

    @Test
    fun `present - handle dismiss`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            val selectedEvent = aTimelineItemEvent()
            initialState.eventSink(ReadReceiptBottomSheetEvent.EventSelected(selectedEvent))
            skipItems(1)
            initialState.eventSink(ReadReceiptBottomSheetEvent.Dismiss)
            assertThat(awaitItem().selectedEvent).isNull()
        }
    }

    private fun createPresenter() = ReadReceiptBottomSheetPresenter()
}
