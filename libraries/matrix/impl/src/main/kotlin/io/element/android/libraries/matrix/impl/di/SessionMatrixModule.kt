/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.api.encryption.EncryptionService
import io.prism.android.libraries.matrix.api.media.PRISMMediaLoader
import io.prism.android.libraries.matrix.api.media.MediaPreviewService
import io.prism.android.libraries.matrix.api.notificationsettings.NotificationSettingsService
import io.prism.android.libraries.matrix.api.room.RoomMembershipObserver
import io.prism.android.libraries.matrix.api.roomdirectory.RoomDirectoryService
import io.prism.android.libraries.matrix.api.roomlist.RoomListService
import io.prism.android.libraries.matrix.api.spaces.SpaceService
import io.prism.android.libraries.matrix.api.sync.SyncService
import io.prism.android.libraries.matrix.api.verification.SessionVerificationService
import kotlinx.coroutines.CoroutineScope

@BindingContainer
@ContributesTo(SessionScope::class)
object SessionPRISMModule {
    @Provides
    fun providesSessionId(matrixClient: PRISMClient): SessionId {
        return matrixClient.sessionId
    }

    @Provides
    fun providesSessionVerificationService(matrixClient: PRISMClient): SessionVerificationService {
        return matrixClient.sessionVerificationService
    }

    @Provides
    fun providesNotificationSettingsService(matrixClient: PRISMClient): NotificationSettingsService {
        return matrixClient.notificationSettingsService
    }

    @Provides
    fun provideRoomMembershipObserver(matrixClient: PRISMClient): RoomMembershipObserver {
        return matrixClient.roomMembershipObserver
    }

    @Provides
    fun providesRoomListService(matrixClient: PRISMClient): RoomListService {
        return matrixClient.roomListService
    }

    @Provides
    fun providesSyncService(matrixClient: PRISMClient): SyncService {
        return matrixClient.syncService
    }

    @Provides
    fun providesEncryptionService(matrixClient: PRISMClient): EncryptionService {
        return matrixClient.encryptionService
    }

    @Provides
    fun providesPRISMMediaLoader(matrixClient: PRISMClient): PRISMMediaLoader {
        return matrixClient.prismMediaLoader
    }

    @SessionCoroutineScope
    @Provides
    fun providesSessionCoroutineScope(matrixClient: PRISMClient): CoroutineScope {
        return matrixClient.sessionCoroutineScope
    }

    @Provides
    fun providesRoomDirectoryService(matrixClient: PRISMClient): RoomDirectoryService {
        return matrixClient.roomDirectoryService
    }

    @Provides
    fun providesMediaPreviewService(matrixClient: PRISMClient): MediaPreviewService {
        return matrixClient.mediaPreviewService
    }

    @Provides
    fun providesSpaceService(matrixClient: PRISMClient): SpaceService {
        return matrixClient.spaceService
    }
}
