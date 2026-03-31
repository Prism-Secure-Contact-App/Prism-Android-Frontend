/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.messagecomposer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.prism.android.libraries.textcomposer.model.MessageComposerMode
import io.prism.android.libraries.textcomposer.model.TextEditorState
import io.prism.android.libraries.textcomposer.model.aTextEditorStateRich
import io.prism.android.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

open class MessageComposerStateProvider : PreviewParameterProvider<MessageComposerState> {
    override val values: Sequence<MessageComposerState>
        get() = sequenceOf(
            aMessageComposerState(),
        )
}

fun aMessageComposerState(
    textEditorState: TextEditorState = aTextEditorStateRich(),
    isFullScreen: Boolean = false,
    mode: MessageComposerMode = MessageComposerMode.Normal,
    showTextFormatting: Boolean = false,
    showAttachmentSourcePicker: Boolean = false,
    canShareLocation: Boolean = true,
    suggestions: ImmutableList<ResolvedSuggestion> = persistentListOf(),
    eventSink: (MessageComposerEvent) -> Unit = {},
) = MessageComposerState(
    textEditorState = textEditorState,
    isFullScreen = isFullScreen,
    mode = mode,
    showTextFormatting = showTextFormatting,
    showAttachmentSourcePicker = showAttachmentSourcePicker,
    canShareLocation = canShareLocation,
    suggestions = suggestions,
    resolveMentionDisplay = { _, _ -> TextDisplay.Plain },
    resolveAtRoomMentionDisplay = { TextDisplay.Plain },
    eventSink = eventSink,
)
