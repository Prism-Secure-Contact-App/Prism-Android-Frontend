/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.messagecomposer

import androidx.compose.runtime.Composable
import io.prism.android.wysiwyg.compose.RichTextEditorState
import io.prism.android.wysiwyg.compose.rememberRichTextEditorState

class TestRichTextEditorStateFactory : RichTextEditorStateFactory {
    @Composable
    override fun remember(): RichTextEditorState {
        return rememberRichTextEditorState("", fake = true)
    }
}
