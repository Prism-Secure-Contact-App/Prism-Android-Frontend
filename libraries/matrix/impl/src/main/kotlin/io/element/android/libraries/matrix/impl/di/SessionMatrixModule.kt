/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.encryption.EncryptionService
import io.prism.android.libraries.prism.api.media.PRISMMediaLoader
import io.prism.android.libraries.prism.api.media.MediaPreviewService
import io.prism.android.libraries.prism.api.notificationsettings.NotificationSettingsService
import io.prism.android.libraries.prism.api.room.RoomMembershipObserver
import io.prism.android.libraries.prism.api.roomdirectory.RoomDirectoryService
import io.prism.android.libraries.prism.api.roomlist.RoomListService
import io.prism.android.libraries.prism.api.spaces.SpaceService
import io.prism.android.libraries.prism.api.sync.SyncService
import io.prism.android.libraries.prism.api.verification.SessionVerificationService
import kotlinx.coroutines.CoroutineScope

@BindingContainer
@ContributesTo(SessionScope::class)
object SessionPRISMModule {
    @Provides
    fun providesSessionId(prismClient: PRISMClient): SessionId {
        return prismClient.sessionId
    }

    @Provides
    fun providesSessionVerificationService(prismClient: PRISMClient): SessionVerificationService {
        return prismClient.sessionVerificationService
    }

    @Provides
    fun providesNotificationSettingsService(prismClient: PRISMClient): NotificationSettingsService {
        return prismClient.notificationSettingsService
    }

    @Provides
    fun provideRoomMembershipObserver(prismClient: PRISMClient): RoomMembershipObserver {
        return prismClient.roomMembershipObserver
    }

    @Provides
    fun providesRoomListService(prismClient: PRISMClient): RoomListService {
        return prismClient.roomListService
    }

    @Provides
    fun providesSyncService(prismClient: PRISMClient): SyncService {
        return prismClient.syncService
    }

    @Provides
    fun providesEncryptionService(prismClient: PRISMClient): EncryptionService {
        return prismClient.encryptionService
    }

    @Provides
    fun providesPRISMMediaLoader(prismClient: PRISMClient): PRISMMediaLoader {
        return prismClient.prismMediaLoader
    }

    @SessionCoroutineScope
    @Provides
    fun providesSessionCoroutineScope(prismClient: PRISMClient): CoroutineScope {
        return prismClient.sessionCoroutineScope
    }

    @Provides
    fun providesRoomDirectoryService(prismClient: PRISMClient): RoomDirectoryService {
        return prismClient.roomDirectoryService
    }

    @Provides
    fun providesMediaPreviewService(prismClient: PRISMClient): MediaPreviewService {
        return prismClient.mediaPreviewService
    }

    @Provides
    fun providesSpaceService(prismClient: PRISMClient): SpaceService {
        return prismClient.spaceService
    }
}
