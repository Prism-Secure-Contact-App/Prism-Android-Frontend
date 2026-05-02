/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.push.impl.notifications

import android.app.Notification
import androidx.core.app.NotificationCompat
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.matrix.test.A_ROOM_ID
import io.prism.android.libraries.push.impl.notifications.factories.aNotificationAccountParams
import io.prism.android.libraries.push.impl.notifications.fake.FakeNotificationCreator
import io.prism.android.services.toolbox.test.strings.FakeStringProvider
import io.prism.android.services.toolbox.test.systemclock.A_FAKE_TIMESTAMP
import io.prism.android.tests.testutils.lambda.any
import io.prism.android.tests.testutils.lambda.nonNull
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultSummaryGroupMessageCreatorTest {
    @Test
    fun `process notifications`() = runTest {
        val notificationCreator = FakeNotificationCreator()
        val summaryCreator = DefaultSummaryGroupMessageCreator(
            stringProvider = FakeStringProvider(),
            notificationCreator = notificationCreator,
        )

        val result = summaryCreator.createSummaryNotification(
            notificationAccountParams = aNotificationAccountParams(),
            roomNotifications = listOf(
                RoomNotification(
                    notification = Notification(),
                    roomId = A_ROOM_ID,
                    messageCount = 1,
                    latestTimestamp = A_FAKE_TIMESTAMP + 10,
                    shouldBing = true,
                    threadId = null,
                )
            ),
            invitationNotifications = emptyList(),
            simpleNotifications = emptyList(),
        )

        notificationCreator.createSummaryListNotificationResult.assertions()
            .isCalledOnce()
            .with(any(), any(), nonNull(), any(), any())

        // Set from the events included
        @Suppress("DEPRECATION")
        assertThat(result.priority).isEqualTo(NotificationCompat.PRIORITY_DEFAULT)
    }
}
