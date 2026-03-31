/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.joinroom.impl

import androidx.compose.runtime.Immutable
import io.prism.android.features.invite.api.InviteData
import io.prism.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.prism.api.core.RoomAlias
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.core.RoomIdOrAlias
import io.prism.android.libraries.prism.api.room.join.JoinRoom
import io.prism.android.libraries.prism.api.room.join.JoinRule
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.model.InviteSender
import kotlinx.collections.immutable.ImmutableList

internal const val MAX_KNOCK_MESSAGE_LENGTH = 500

data class JoinRoomState(
    val roomIdOrAlias: RoomIdOrAlias,
    val contentState: ContentState,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val joinAction: AsyncAction<Unit>,
    val knockAction: AsyncAction<Unit>,
    val forgetAction: AsyncAction<Unit>,
    val cancelKnockAction: AsyncAction<Unit>,
    private val applicationName: String,
    val knockMessage: String,
    val hideInviteAvatars: Boolean,
    val canReportRoom: Boolean,
    val eventSink: (JoinRoomEvents) -> Unit
) {
    val isJoinActionUnauthorized = joinAction is AsyncAction.Failure && joinAction.error is JoinRoom.Failures.UnauthorizedJoin
    val joinAuthorisationStatus = when (contentState) {
        is ContentState.Loaded -> {
            when {
                isJoinActionUnauthorized -> {
                    JoinAuthorisationStatus.Unauthorized
                }
                else -> {
                    contentState.joinAuthorisationStatus
                }
            }
        }
        is ContentState.UnknownRoom -> {
            if (isJoinActionUnauthorized) {
                JoinAuthorisationStatus.Unauthorized
            } else {
                JoinAuthorisationStatus.Unknown
            }
        }
        else -> JoinAuthorisationStatus.None
    }

    val hideAvatarsImages = hideInviteAvatars && joinAuthorisationStatus is JoinAuthorisationStatus.IsInvited
}

@Immutable
sealed interface ContentState {
    data object Dismissing : ContentState
    data object Loading : ContentState
    data class Failure(val error: Throwable) : ContentState
    data object UnknownRoom : ContentState
    data class Loaded(
        val roomId: RoomId,
        val name: String?,
        val topic: String?,
        val alias: RoomAlias?,
        val numberOfMembers: Long?,
        val roomAvatarUrl: String?,
        val joinAuthorisationStatus: JoinAuthorisationStatus,
        val joinRule: JoinRule?,
        val details: LoadedDetails,
    ) : ContentState {
        val showMemberCount = numberOfMembers != null
        val isSpace = details is LoadedDetails.Space

        fun avatarData(size: AvatarSize): AvatarData {
            return AvatarData(
                id = roomId.value,
                name = name,
                url = roomAvatarUrl,
                size = size,
            )
        }
    }
}

@Immutable
sealed interface LoadedDetails {
    data class Room(
        val isDm: Boolean,
    ) : LoadedDetails

    data class Space(
        val childrenCount: Int,
        val heroes: ImmutableList<PRISMUser>,
    ) : LoadedDetails
}

sealed interface JoinAuthorisationStatus {
    data object None : JoinAuthorisationStatus
    data class IsInvited(val inviteData: InviteData, val inviteSender: InviteSender?) : JoinAuthorisationStatus
    data class IsBanned(val banSender: InviteSender?, val reason: String?) : JoinAuthorisationStatus
    data object IsKnocked : JoinAuthorisationStatus
    data object CanKnock : JoinAuthorisationStatus
    data object CanJoin : JoinAuthorisationStatus
    data object NeedInvite : JoinAuthorisationStatus
    data object Restricted : JoinAuthorisationStatus
    data object Unknown : JoinAuthorisationStatus
    data object Unauthorized : JoinAuthorisationStatus
}
