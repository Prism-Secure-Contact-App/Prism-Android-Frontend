/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.room

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.history.RoomHistoryVisibility
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.room.powerlevels.RoomPowerLevels
import io.prism.android.libraries.prism.api.room.tombstone.SuccessorRoom
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class RoomInfo(
    val id: RoomId,
    /** The room's name from the room state event if received from sync, or one that's been computed otherwise. */
    val name: String?,
    /** Room name as defined by the room state event only. */
    val rawName: String?,
    val topic: String?,
    val avatarUrl: String?,
    val isPublic: Boolean?,
    val isDirect: Boolean,
    val isEncrypted: Boolean?,
    val joinRule: JoinRule?,
    val isSpace: Boolean,
    val isFavorite: Boolean,
    val canonicalAlias: RoomAlias?,
    val alternativeAliases: ImmutableList<RoomAlias>,
    val currentUserMembership: CurrentUserMembership,
    /**
     * Member who invited the current user to a room that's in the invited
     * state.
     *
     * Can be missing if the room membership invite event is missing from the
     * store.
     */
    val inviter: RoomMember?,
    val activeMembersCount: Long,
    val invitedMembersCount: Long,
    val joinedMembersCount: Long,
    val roomPowerLevels: RoomPowerLevels?,
    val highlightCount: Long,
    val notificationCount: Long,
    val userDefinedNotificationMode: RoomNotificationMode?,
    val hasRoomCall: Boolean,
    val activeRoomCallParticipants: ImmutableList<UserId>,
    val isMarkedUnread: Boolean,
    /**
     * "Interesting" messages received in that room, independently of the
     * notification settings.
     */
    val numUnreadMessages: Long,
    /**
     * Events that will notify the user, according to their
     * notification settings.
     */
    val numUnreadNotifications: Long,
    /**
     * Events causing mentions/highlights for the user, according to their
     * notification settings.
     */
    val numUnreadMentions: Long,
    val heroes: ImmutableList<PRISMUser>,
    val pinnedEventIds: ImmutableList<EventId>,
    val creators: ImmutableList<UserId>,
    val historyVisibility: RoomHistoryVisibility,
    val successorRoom: SuccessorRoom?,
    val roomVersion: String?,
    val privilegedCreatorRole: Boolean,
    val isLowPriority: Boolean,
    val activeCallIntentConsensus: CallIntentConsensus,
) {
    val aliases: List<RoomAlias>
        get() = listOfNotNull(canonicalAlias) + alternativeAliases
}
