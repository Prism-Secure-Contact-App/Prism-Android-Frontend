/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.call.notifications

import androidx.core.graphics.drawable.IconCompat
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import com.google.common.truth.Truth.assertThat
import io.prism.android.features.call.impl.notifications.RingingCallNotificationCreator
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.matrix.test.AN_EVENT_ID
import io.prism.android.libraries.matrix.test.A_ROOM_ID
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.A_USER_ID_2
import io.prism.android.libraries.matrix.test.FakePRISMClient
import io.prism.android.libraries.matrix.test.FakePRISMClientProvider
import io.prism.android.libraries.matrix.ui.media.test.FakeImageLoaderHolder
import io.prism.android.libraries.push.test.notifications.push.FakeNotificationBitmapLoader
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RingingCallNotificationCreatorTest {
    @Test
    fun `createNotification - with no associated PRISMClient does nothing`() = runTest {
        val notificationCreator = createRingingCallNotificationCreator(
            prismClientProvider = FakePRISMClientProvider(getClient = { Result.failure(IllegalStateException("No client found")) })
        )

        val result = notificationCreator.createTestNotification()

        assertThat(result).isNull()
    }

    @Test
    fun `createNotification - creates a valid notification`() = runTest {
        val notificationCreator = createRingingCallNotificationCreator(
            prismClientProvider = FakePRISMClientProvider(getClient = { Result.success(FakePRISMClient()) })
        )

        val result = notificationCreator.createTestNotification()

        assertThat(result).isNotNull()
    }

    @Test
    fun `createNotification - tries to load the avatar URL`() = runTest {
        val getUserIconLambda = lambdaRecorder<AvatarData, ImageLoader, IconCompat?> { _, _ -> null }
        val notificationCreator = createRingingCallNotificationCreator(
            prismClientProvider = FakePRISMClientProvider(getClient = { Result.success(FakePRISMClient()) }),
            notificationBitmapLoader = FakeNotificationBitmapLoader(getUserIconResult = getUserIconLambda)
        )

        notificationCreator.createTestNotification()

        getUserIconLambda.assertions().isCalledOnce()
    }

    @Test
    fun `createNotification - use the correct style for video call`() = runTest {
        val notificationCreator = createRingingCallNotificationCreator(
            prismClientProvider = FakePRISMClientProvider(getClient = { Result.success(FakePRISMClient()) }),
        )

        val notification = notificationCreator.createTestNotification()
        assertThat(notification?.category).isEqualTo("call")

        val acceptAction = notification?.actions?.get(1)
        assertThat(acceptAction?.title?.toString()).isEqualTo("Video")
    }

    @Test
    fun `createNotification - use the correct style for audio call`() = runTest {
        val notificationCreator = createRingingCallNotificationCreator(
            prismClientProvider = FakePRISMClientProvider(getClient = { Result.success(FakePRISMClient()) }),
        )

        val notification = notificationCreator.createTestNotification(audioOnly = true)
        assertThat(notification?.category).isEqualTo("call")

        val acceptAction = notification?.actions?.get(1)
        assertThat(acceptAction?.title?.toString()).isEqualTo("Answer")
    }

    private suspend fun RingingCallNotificationCreator.createTestNotification(audioOnly: Boolean = false) = createNotification(
        sessionId = A_SESSION_ID,
        roomId = A_ROOM_ID,
        eventId = AN_EVENT_ID,
        senderId = A_USER_ID_2,
        roomName = "Room",
        senderDisplayName = "Johnnie Murphy",
        roomAvatarUrl = "https://example.com/avatar.jpg",
        notificationChannelId = "channelId",
        timestamp = 0L,
        expirationTimestamp = 20L,
        textContent = "textContent",
        audioOnly = audioOnly
    )

    private fun createRingingCallNotificationCreator(
        prismClientProvider: FakePRISMClientProvider = FakePRISMClientProvider(),
        imageLoaderHolder: FakeImageLoaderHolder = FakeImageLoaderHolder(),
        notificationBitmapLoader: FakeNotificationBitmapLoader = FakeNotificationBitmapLoader(),
    ) = RingingCallNotificationCreator(
        context = InstrumentationRegistry.getInstrumentation().targetContext,
        prismClientProvider = prismClientProvider,
        imageLoaderHolder = imageLoaderHolder,
        notificationBitmapLoader = notificationBitmapLoader,
    )
}
