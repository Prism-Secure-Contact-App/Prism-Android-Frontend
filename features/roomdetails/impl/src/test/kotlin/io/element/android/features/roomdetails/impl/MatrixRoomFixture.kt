/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetails.impl

import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.RoomMember
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.room.powerlevels.RoomPermissions
import io.prism.android.libraries.prism.test.AN_AVATAR_URL
import io.prism.android.libraries.prism.test.A_ROOM_ALIAS
import io.prism.android.libraries.prism.test.A_ROOM_ID
import io.prism.android.libraries.prism.test.A_ROOM_NAME
import io.prism.android.libraries.prism.test.A_ROOM_TOPIC
import io.prism.android.libraries.prism.test.A_SESSION_ID
import io.prism.android.libraries.prism.test.notificationsettings.FakeNotificationSettingsService
import io.prism.android.libraries.prism.test.room.FakeBaseRoom
import io.prism.android.libraries.prism.test.room.FakeJoinedRoom
import io.prism.android.libraries.prism.test.room.aRoomInfo
import io.prism.android.libraries.prism.test.room.powerlevels.FakeRoomPermissions
import io.prism.android.tests.testutils.lambda.lambdaError

fun aRoom(
    sessionId: SessionId = A_SESSION_ID,
    roomId: RoomId = A_ROOM_ID,
    displayName: String = A_ROOM_NAME,
    rawName: String? = displayName,
    topic: String? = A_ROOM_TOPIC,
    avatarUrl: String? = AN_AVATAR_URL,
    canonicalAlias: RoomAlias? = A_ROOM_ALIAS,
    roomPermissions: RoomPermissions = FakeRoomPermissions(),
    isEncrypted: Boolean = true,
    isPublic: Boolean = true,
    isDirect: Boolean = false,
    joinRule: JoinRule? = null,
    activeMemberCount: Long = 1,
    joinedMemberCount: Long = 1,
    invitedMemberCount: Long = 0,
    userDisplayNameResult: (UserId) -> Result<String?> = { lambdaError() },
    userAvatarUrlResult: () -> Result<String?> = { lambdaError() },
    getUpdatedMemberResult: (UserId) -> Result<RoomMember> = { lambdaError() },
    userRoleResult: () -> Result<RoomMember.Role> = { lambdaError() },
    setIsFavoriteResult: (Boolean) -> Result<Unit> = { lambdaError() },
) = FakeBaseRoom(
    sessionId = sessionId,
    roomId = roomId,
    userDisplayNameResult = userDisplayNameResult,
    userAvatarUrlResult = userAvatarUrlResult,
    getUpdatedMemberResult = getUpdatedMemberResult,
    userRoleResult = userRoleResult,
    setIsFavoriteResult = setIsFavoriteResult,
    roomPermissions = roomPermissions,
    initialRoomInfo = aRoomInfo(
        name = displayName,
        rawName = rawName,
        topic = topic,
        avatarUrl = avatarUrl,
        canonicalAlias = canonicalAlias,
        isDirect = isDirect,
        isPublic = isPublic,
        isEncrypted = isEncrypted,
        joinRule = joinRule,
        joinedMembersCount = joinedMemberCount,
        activeMembersCount = activeMemberCount,
        invitedMembersCount = invitedMemberCount,
    )
)

fun aJoinedRoom(
    sessionId: SessionId = A_SESSION_ID,
    roomId: RoomId = A_ROOM_ID,
    displayName: String = A_ROOM_NAME,
    rawName: String? = displayName,
    topic: String? = A_ROOM_TOPIC,
    avatarUrl: String? = AN_AVATAR_URL,
    canonicalAlias: RoomAlias? = A_ROOM_ALIAS,
    roomPermissions: RoomPermissions = FakeRoomPermissions(),
    isEncrypted: Boolean = true,
    isPublic: Boolean = true,
    isDirect: Boolean = false,
    joinRule: JoinRule? = null,
    activeMemberCount: Long = 1,
    joinedMemberCount: Long = 1,
    invitedMemberCount: Long = 0,
    notificationSettingsService: FakeNotificationSettingsService = FakeNotificationSettingsService(),
    userDisplayNameResult: (UserId) -> Result<String?> = { lambdaError() },
    userAvatarUrlResult: () -> Result<String?> = { lambdaError() },
    setNameResult: (String) -> Result<Unit> = { lambdaError() },
    setTopicResult: (String) -> Result<Unit> = { lambdaError() },
    updateAvatarResult: (String, ByteArray) -> Result<Unit> = { _, _ -> lambdaError() },
    removeAvatarResult: () -> Result<Unit> = { lambdaError() },
    getUpdatedMemberResult: (UserId) -> Result<RoomMember> = { lambdaError() },
    userRoleResult: () -> Result<RoomMember.Role> = { lambdaError() },
    kickUserResult: (UserId, String?) -> Result<Unit> = { _, _ -> lambdaError() },
    banUserResult: (UserId, String?) -> Result<Unit> = { _, _ -> lambdaError() },
    unBanUserResult: (UserId, String?) -> Result<Unit> = { _, _ -> lambdaError() },
    updateCanonicalAliasResult: (RoomAlias?, List<RoomAlias>) -> Result<Unit> = { _, _ -> lambdaError() },
    publishRoomAliasInRoomDirectoryResult: (RoomAlias) -> Result<Boolean> = { lambdaError() },
    removeRoomAliasFromRoomDirectoryResult: (RoomAlias) -> Result<Boolean> = { lambdaError() },
    setIsFavoriteResult: (Boolean) -> Result<Unit> = { lambdaError() },
) = FakeJoinedRoom(
    roomNotificationSettingsService = notificationSettingsService,
    setNameResult = setNameResult,
    setTopicResult = setTopicResult,
    updateAvatarResult = updateAvatarResult,
    removeAvatarResult = removeAvatarResult,
    kickUserResult = kickUserResult,
    banUserResult = banUserResult,
    unBanUserResult = unBanUserResult,
    updateCanonicalAliasResult = updateCanonicalAliasResult,
    publishRoomAliasInRoomDirectoryResult = publishRoomAliasInRoomDirectoryResult,
    removeRoomAliasFromRoomDirectoryResult = removeRoomAliasFromRoomDirectoryResult,
    baseRoom = aRoom(
        sessionId = sessionId,
        roomId = roomId,
        roomPermissions = roomPermissions,
        userDisplayNameResult = userDisplayNameResult,
        userAvatarUrlResult = userAvatarUrlResult,
        getUpdatedMemberResult = getUpdatedMemberResult,
        userRoleResult = userRoleResult,
        setIsFavoriteResult = setIsFavoriteResult,
        displayName = displayName,
        rawName = rawName,
        topic = topic,
        avatarUrl = avatarUrl,
        canonicalAlias = canonicalAlias,
        isDirect = isDirect,
        isPublic = isPublic,
        isEncrypted = isEncrypted,
        joinRule = joinRule,
        joinedMemberCount = joinedMemberCount,
        activeMemberCount = activeMemberCount,
        invitedMemberCount = invitedMemberCount,
    )
)
