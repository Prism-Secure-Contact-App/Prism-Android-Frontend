/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.prism.android.features.roomdetails.impl.members.details.RoomMemberDetailsPresenter
import io.prism.android.features.userprofile.api.UserProfilePresenterFactory
import io.prism.android.libraries.androidutils.clipboard.ClipboardHelper
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.encryption.EncryptionService
import io.prism.android.libraries.prism.api.room.JoinedRoom

@BindingContainer
@ContributesTo(RoomScope::class)
object RoomMemberModule {
    @Provides
    fun provideRoomMemberDetailsPresenterFactory(
        room: JoinedRoom,
        userProfilePresenterFactory: UserProfilePresenterFactory,
        encryptionService: EncryptionService,
        clipboardHelper: ClipboardHelper,
    ): RoomMemberDetailsPresenter.Factory {
        return object : RoomMemberDetailsPresenter.Factory {
            override fun create(roomMemberId: UserId): RoomMemberDetailsPresenter {
                return RoomMemberDetailsPresenter(
                    roomMemberId = roomMemberId,
                    room = room,
                    userProfilePresenterFactory = userProfilePresenterFactory,
                    encryptionService = encryptionService,
                    clipboardHelper = clipboardHelper,
                )
            }
        }
    }
}
