/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import io.prism.android.features.messages.api.timeline.voicemessages.composer.aVoiceMessageComposerState
import io.prism.android.features.messages.api.timeline.voicemessages.composer.aVoiceMessagePreviewState
import io.prism.android.features.messages.impl.actionlist.ActionListState
import io.prism.android.features.messages.impl.actionlist.anActionListState
import io.prism.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.prism.android.features.messages.impl.crypto.identity.aRoomMemberIdentityStateChange
import io.prism.android.features.messages.impl.crypto.identity.anIdentityChangeState
import io.prism.android.features.messages.impl.link.LinkState
import io.prism.android.features.messages.impl.link.aLinkState
import io.prism.android.features.messages.impl.messagecomposer.MessageComposerState
import io.prism.android.features.messages.impl.messagecomposer.aMessageComposerState
import io.prism.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.prism.android.features.messages.impl.pinned.banner.aLoadedPinnedMessagesBannerState
import io.prism.android.features.messages.impl.timeline.TimelineState
import io.prism.android.features.messages.impl.timeline.aTimelineItemList
import io.prism.android.features.messages.impl.timeline.aTimelineState
import io.prism.android.features.messages.impl.timeline.components.customreaction.CustomReactionEvent
import io.prism.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.prism.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryEvent
import io.prism.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetEvent
import io.prism.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.prism.android.features.messages.impl.timeline.model.TimelineItem
import io.prism.android.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import io.prism.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.prism.android.features.messages.impl.timeline.protection.aTimelineProtectionState
import io.prism.android.features.roomcall.api.RoomCallState
import io.prism.android.features.roomcall.api.aStandByCallState
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationEvents
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationPermissions
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationState
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.libraries.matrix.api.core.ThreadId
import io.prism.android.libraries.matrix.api.encryption.identity.IdentityState
import io.prism.android.libraries.matrix.api.room.tombstone.SuccessorRoom
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.libraries.textcomposer.model.MessageComposerMode
import io.prism.android.libraries.textcomposer.model.aTextEditorStateMarkdown
import io.prism.android.libraries.textcomposer.model.aTextEditorStateRich
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

open class MessagesStateProvider : PreviewParameterProvider<MessagesState> {
    override val values: Sequence<MessagesState>
        get() = sequenceOf(
            aMessagesState(),
            aMessagesState(composerState = aMessageComposerState(showAttachmentSourcePicker = true)),
            aMessagesState(userEventPermissions = aUserEventPermissions(canSendMessage = false)),
            aMessagesState(showReinvitePrompt = true),
            aMessagesState(composerState = aMessageComposerState(showTextFormatting = true)),
            aMessagesState(
                voiceMessageComposerState = aVoiceMessageComposerState(showPermissionRationaleDialog = true),
            ),
            aMessagesState(
                voiceMessageComposerState = aVoiceMessageComposerState(
                    voiceMessageState = aVoiceMessagePreviewState(),
                    showSendFailureDialog = true
                ),
            ),
            aMessagesState(
                pinnedMessagesBannerState = aLoadedPinnedMessagesBannerState(
                    knownPinnedMessagesCount = 4,
                    currentPinnedMessageIndex = 0,
                ),
            ),
            aMessagesState(successorRoom = SuccessorRoom(RoomId("!id:domain"), null)),
            aMessagesState(
                timelineState = aTimelineState(
                    timelineMode = Timeline.Mode.Thread(threadRootId = ThreadId("\$a-thread-id")),
                    timelineItems = aTimelineItemList(aTimelineItemTextContent()),
                )
            ),
            aMessagesState(
                composerState = aMessageComposerState(textEditorState = aTextEditorStateMarkdown()),
                identityChangeState = anIdentityChangeState(listOf(aRoomMemberIdentityStateChange()))
            ),
        )
}

fun aMessagesState(
    roomName: String? = "Room name",
    roomAvatar: AvatarData = AvatarData("!id:domain", "Room name", size = AvatarSize.TimelineRoom),
    userEventPermissions: UserEventPermissions = aUserEventPermissions(),
    composerState: MessageComposerState = aMessageComposerState(
        textEditorState = aTextEditorStateRich(initialText = "Hello", initialFocus = true),
        isFullScreen = false,
        mode = MessageComposerMode.Normal,
    ),
    voiceMessageComposerState: VoiceMessageComposerState = aVoiceMessageComposerState(),
    timelineState: TimelineState = aTimelineState(
        timelineItems = aTimelineItemList(aTimelineItemTextContent()),
        // Render a focused event for an event with sender information displayed
        focusedEventIndex = 2,
    ),
    timelineProtectionState: TimelineProtectionState = aTimelineProtectionState(),
    identityChangeState: IdentityChangeState = anIdentityChangeState(),
    linkState: LinkState = aLinkState(),
    readReceiptBottomSheetState: ReadReceiptBottomSheetState = aReadReceiptBottomSheetState(),
    actionListState: ActionListState = anActionListState(),
    customReactionState: CustomReactionState = aCustomReactionState(),
    reactionSummaryState: ReactionSummaryState = aReactionSummaryState(),
    showReinvitePrompt: Boolean = false,
    roomCallState: RoomCallState = aStandByCallState(),
    pinnedMessagesBannerState: PinnedMessagesBannerState = aLoadedPinnedMessagesBannerState(),
    dmUserVerificationState: IdentityState? = null,
    roomMemberModerationState: RoomMemberModerationState = aRoomMemberModerationState(),
    topBarSharedHistoryIcon: SharedHistoryIcon = SharedHistoryIcon.NONE,
    successorRoom: SuccessorRoom? = null,
    eventSink: (MessagesEvent) -> Unit = {},
) = MessagesState(
    roomId = RoomId("!id:domain"),
    roomName = roomName,
    roomAvatar = roomAvatar,
    heroes = persistentListOf(),
    userEventPermissions = userEventPermissions,
    composerState = composerState,
    voiceMessageComposerState = voiceMessageComposerState,
    timelineProtectionState = timelineProtectionState,
    identityChangeState = identityChangeState,
    linkState = linkState,
    timelineState = timelineState,
    readReceiptBottomSheetState = readReceiptBottomSheetState,
    actionListState = actionListState,
    customReactionState = customReactionState,
    reactionSummaryState = reactionSummaryState,
    snackbarMessage = null,
    inviteProgress = AsyncData.Uninitialized,
    showReinvitePrompt = showReinvitePrompt,
    enableTextFormatting = true,
    roomCallState = roomCallState,
    appName = "PRISM",
    pinnedMessagesBannerState = pinnedMessagesBannerState,
    dmUserVerificationState = dmUserVerificationState,
    roomMemberModerationState = roomMemberModerationState,
    topBarSharedHistoryIcon = topBarSharedHistoryIcon,
    successorRoom = successorRoom,
    eventSink = eventSink,
)

fun aRoomMemberModerationState(
    permissions: RoomMemberModerationPermissions = RoomMemberModerationPermissions.DEFAULT,
) = object : RoomMemberModerationState {
    override val permissions: RoomMemberModerationPermissions = permissions
    override val eventSink: (RoomMemberModerationEvents) -> Unit = {}
}

fun aUserEventPermissions(
    canRedactOwn: Boolean = false,
    canRedactOther: Boolean = false,
    canSendMessage: Boolean = true,
    canSendReaction: Boolean = true,
    canPinUnpin: Boolean = false,
) = UserEventPermissions(
    canRedactOwn = canRedactOwn,
    canRedactOther = canRedactOther,
    canSendMessage = canSendMessage,
    canSendReaction = canSendReaction,
    canPinUnpin = canPinUnpin,
)

fun aReactionSummaryState(
    target: ReactionSummaryState.Summary? = null,
    eventSink: (ReactionSummaryEvent) -> Unit = {}
) = ReactionSummaryState(
    target = target,
    eventSink = eventSink,
)

fun aCustomReactionState(
    target: CustomReactionState.Target = CustomReactionState.Target.None,
    recentEmojis: ImmutableList<String> = persistentListOf(),
    eventSink: (CustomReactionEvent) -> Unit = {},
) = CustomReactionState(
    target = target,
    recentEmojis = recentEmojis,
    selectedEmoji = persistentSetOf(),
    eventSink = eventSink,
)

fun aReadReceiptBottomSheetState(
    selectedEvent: TimelineItem.Event? = null,
    eventSink: (ReadReceiptBottomSheetEvent) -> Unit = {},
) = ReadReceiptBottomSheetState(
    selectedEvent = selectedEvent,
    eventSink = eventSink,
)
