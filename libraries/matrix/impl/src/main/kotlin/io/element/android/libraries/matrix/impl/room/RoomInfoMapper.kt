/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.CurrentUserMembership
import io.prism.android.libraries.prism.api.room.RoomInfo
import io.prism.android.libraries.prism.api.room.RoomNotificationMode
import io.prism.android.libraries.prism.api.room.powerlevels.RoomPowerLevels
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.impl.room.history.map
import io.prism.android.libraries.prism.impl.room.join.map
import io.prism.android.libraries.prism.impl.room.member.RoomMemberMapper
import io.prism.android.libraries.prism.impl.room.powerlevels.RoomPowerLevelsValuesMapper
import io.prism.android.libraries.prism.impl.room.tombstone.map
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import org.prism.rustcomponents.sdk.Membership
import org.prism.rustcomponents.sdk.RoomHero
import uniffi.prism_sdk_base.EncryptionState
import org.prism.rustcomponents.sdk.Membership as RustMembership
import org.prism.rustcomponents.sdk.RoomInfo as RustRoomInfo
import org.prism.rustcomponents.sdk.RoomNotificationMode as RustRoomNotificationMode
import org.prism.rustcomponents.sdk.RoomPowerLevels as RustRoomPowerLevels

class RoomInfoMapper {
    fun map(rustRoomInfo: RustRoomInfo): RoomInfo = rustRoomInfo.let {
        return RoomInfo(
            id = RoomId(it.id),
            creators = it.creators.orEmpty().map(::UserId).toImmutableList(),
            name = it.displayName,
            rawName = it.rawName,
            topic = it.topic,
            avatarUrl = it.avatarUrl,
            isPublic = it.isPublic,
            isDirect = it.isDirect,
            isEncrypted = when (it.encryptionState) {
                EncryptionState.ENCRYPTED -> true
                EncryptionState.NOT_ENCRYPTED -> false
                EncryptionState.UNKNOWN -> null
            },
            joinRule = it.joinRule?.map(),
            isSpace = it.isSpace,
            isFavorite = it.isFavourite,
            canonicalAlias = it.canonicalAlias?.let(::RoomAlias),
            alternativeAliases = it.alternativeAliases.map(::RoomAlias).toImmutableList(),
            currentUserMembership = it.membership.map(),
            inviter = it.inviter?.let(RoomMemberMapper::map),
            activeMembersCount = it.activeMembersCount.toLong(),
            invitedMembersCount = it.invitedMembersCount.toLong(),
            joinedMembersCount = it.joinedMembersCount.toLong(),
            roomPowerLevels = it.powerLevels?.let(::mapPowerLevels),
            highlightCount = it.highlightCount.toLong(),
            notificationCount = it.notificationCount.toLong(),
            userDefinedNotificationMode = it.cachedUserDefinedNotificationMode?.map(),
            hasRoomCall = it.hasRoomCall,
            activeRoomCallParticipants = it.activeRoomCallParticipants.map(::UserId).toImmutableList(),
            heroes = it.prismHeroes().toImmutableList(),
            pinnedEventIds = it.pinnedEventIds.map(::EventId).toImmutableList(),
            isMarkedUnread = it.isMarkedUnread,
            numUnreadMessages = it.numUnreadMessages.toLong(),
            numUnreadMentions = it.numUnreadMentions.toLong(),
            numUnreadNotifications = it.numUnreadNotifications.toLong(),
            historyVisibility = it.historyVisibility.map(),
            successorRoom = it.successorRoom?.map(),
            roomVersion = it.roomVersion,
            privilegedCreatorRole = it.privilegedCreatorsRole,
            isLowPriority = it.isLowPriority,
            activeCallIntentConsensus = it.activeRoomCallConsensusIntent.map(),
        )
    }
}

fun RustMembership.map(): CurrentUserMembership = when (this) {
    RustMembership.INVITED -> CurrentUserMembership.INVITED
    RustMembership.JOINED -> CurrentUserMembership.JOINED
    RustMembership.LEFT -> CurrentUserMembership.LEFT
    Membership.KNOCKED -> CurrentUserMembership.KNOCKED
    RustMembership.BANNED -> CurrentUserMembership.BANNED
}

fun RustRoomNotificationMode.map(): RoomNotificationMode = when (this) {
    RustRoomNotificationMode.ALL_MESSAGES -> RoomNotificationMode.ALL_MESSAGES
    RustRoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY -> RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY
    RustRoomNotificationMode.MUTE -> RoomNotificationMode.MUTE
}

/**
 * Map a RoomHero to a PRISMUser. There is not need to create a RoomHero type on the application side.
 */
fun RoomHero.map(): PRISMUser = PRISMUser(
    userId = UserId(userId),
    displayName = displayName,
    avatarUrl = avatarUrl
)

fun mapPowerLevels(roomPowerLevels: RustRoomPowerLevels): RoomPowerLevels {
    return RoomPowerLevels(
        values = RoomPowerLevelsValuesMapper.map(roomPowerLevels.values()),
        users = roomPowerLevels.userPowerLevels().mapKeys { (key, _) -> UserId(key) }.toImmutableMap()
    )
}
