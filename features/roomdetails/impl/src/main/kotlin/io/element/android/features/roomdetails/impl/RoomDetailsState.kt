/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl

import androidx.compose.runtime.Immutable
import io.prism.android.features.leaveroom.api.LeaveRoomState
import io.prism.android.features.roomcall.api.RoomCallState
import io.prism.android.features.userprofile.api.UserProfileState
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.api.room.RoomNotificationSettings
import io.prism.android.libraries.prism.api.room.history.RoomHistoryVisibility
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class RoomDetailsState(
    val roomId: RoomId,
    val roomName: String,
    val roomAlias: RoomAlias?,
    val roomAvatarUrl: String?,
    val roomTopic: RoomTopicState,
    val memberCount: Long,
    val isEncrypted: Boolean,
    val roomType: RoomDetailsType,
    val roomMemberDetailsState: UserProfileState?,
    val canEdit: Boolean,
    val canInvite: Boolean,
    val roomCallState: RoomCallState,
    val leaveRoomState: LeaveRoomState,
    val roomNotificationSettings: RoomNotificationSettings?,
    val isFavorite: Boolean,
    val displayRolesAndPermissionsSettings: Boolean,
    val isPublic: Boolean,
    val heroes: ImmutableList<PRISMUser>,
    val pinnedMessagesCount: Int?,
    val snackbarMessage: SnackbarMessage?,
    val canShowKnockRequests: Boolean,
    val knockRequestsCount: Int?,
    val canShowSecurityAndPrivacy: Boolean,
    val hasMemberVerificationViolations: Boolean,
    val canReportRoom: Boolean,
    val isTombstoned: Boolean,
    val showDebugInfo: Boolean,
    val roomVersion: String?,
    val enableKeyShareOnInvite: Boolean,
    val roomHistoryVisibility: RoomHistoryVisibility,
    val eventSink: (RoomDetailsEvent) -> Unit
) {
    val roomBadges = buildList {
        if (isEncrypted) {
            add(RoomBadge.ENCRYPTED)
        } else {
            add(RoomBadge.NOT_ENCRYPTED)
        }
        if (isPublic) {
            add(RoomBadge.PUBLIC)
        }
        if (enableKeyShareOnInvite && isEncrypted) {
            when (roomHistoryVisibility) {
                RoomHistoryVisibility.Invited, RoomHistoryVisibility.Joined -> add(RoomBadge.SHARED_HISTORY_HIDDEN)
                RoomHistoryVisibility.Shared -> add(RoomBadge.SHARED_HISTORY_SHARED)
                RoomHistoryVisibility.WorldReadable -> add(RoomBadge.SHARED_HISTORY_WORLD_READABLE)
                else -> {}
            }
        }
    }.toImmutableList()
}

@Immutable
sealed interface RoomDetailsType {
    data object Room : RoomDetailsType
    data class Dm(
        val me: RoomMember,
        val otherMember: RoomMember,
    ) : RoomDetailsType
}

@Immutable
sealed interface RoomTopicState {
    data object Hidden : RoomTopicState
    data object CanAddTopic : RoomTopicState
    data class ExistingTopic(val topic: String) : RoomTopicState
}

enum class RoomBadge {
    ENCRYPTED,
    NOT_ENCRYPTED,
    PUBLIC,
    SHARED_HISTORY_HIDDEN,
    SHARED_HISTORY_SHARED,
    SHARED_HISTORY_WORLD_READABLE
}
