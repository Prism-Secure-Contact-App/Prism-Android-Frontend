/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.crypto.identity

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.features.messages.impl.MessagesView
import io.prism.android.features.messages.impl.aMessagesState
import io.prism.android.features.messages.impl.messagecomposer.aMessageComposerState
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.textcomposer.model.aTextEditorStateMarkdown

@PreviewsDayNight
@Composable
internal fun MessagesViewWithIdentityChangePreview(
    @PreviewParameter(IdentityChangeStateProvider::class) identityChangeState: IdentityChangeState
) = PRISMPreview {
    MessagesView(
        state = aMessagesState(
            composerState = aMessageComposerState(
                textEditorState = aTextEditorStateMarkdown(
                    initialText = "",
                    initialFocus = false,
                )
            ),
            identityChangeState = identityChangeState,
        ),
        onBackClick = {},
        onRoomDetailsClick = {},
        onEventContentClick = { _, _ -> false },
        onUserDataClick = {},
        onLinkClick = { _, _ -> },
        onSendLocationClick = {},
        onCreatePollClick = {},
        onJoinCallClick = {},
        onViewAllPinnedMessagesClick = {},
        knockRequestsBannerView = {}
    )
}
