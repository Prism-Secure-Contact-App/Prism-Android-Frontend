/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import io.prism.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import io.prism.android.features.messages.impl.actionlist.ActionListState
import io.prism.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.prism.android.features.messages.impl.link.LinkState
import io.prism.android.features.messages.impl.messagecomposer.MessageComposerState
import io.prism.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.prism.android.features.messages.impl.timeline.TimelineState
import io.prism.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.prism.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.prism.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.prism.android.features.roomcall.api.RoomCallState
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationState
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.prism.api.encryption.identity.IdentityState
import io.prism.android.libraries.prism.api.room.tombstone.SuccessorRoom
import kotlinx.collections.immutable.ImmutableList

data class MessagesState(
    val roomId: RoomId,
    val roomName: String?,
    val roomAvatar: AvatarData,
    val heroes: ImmutableList<AvatarData>,
    val userEventPermissions: UserEventPermissions,
    val composerState: MessageComposerState,
    val voiceMessageComposerState: VoiceMessageComposerState,
    val timelineState: TimelineState,
    val timelineProtectionState: TimelineProtectionState,
    val identityChangeState: IdentityChangeState,
    val linkState: LinkState,
    val actionListState: ActionListState,
    val customReactionState: CustomReactionState,
    val reactionSummaryState: ReactionSummaryState,
    val readReceiptBottomSheetState: ReadReceiptBottomSheetState,
    val snackbarMessage: SnackbarMessage?,
    val inviteProgress: AsyncData<Unit>,
    val showReinvitePrompt: Boolean,
    val enableTextFormatting: Boolean,
    val roomCallState: RoomCallState,
    val appName: String,
    val pinnedMessagesBannerState: PinnedMessagesBannerState,
    val dmUserVerificationState: IdentityState?,
    val roomMemberModerationState: RoomMemberModerationState,
    /** Type of "shared history" icon to show in the top bar. */
    val topBarSharedHistoryIcon: SharedHistoryIcon,
    val successorRoom: SuccessorRoom?,
    val eventSink: (MessagesEvent) -> Unit
) {
    val isTombstoned = successorRoom != null
}

/** Type of "shared history" icon to show in the top bar. */
enum class SharedHistoryIcon {
    /** Show no icon at all. */
    NONE,

    /** history_visibility: shared. */
    SHARED,

    /** history_visibility: world_readable. */
    WORLD_READABLE
}
