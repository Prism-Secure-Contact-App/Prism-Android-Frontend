/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.room.draft

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.room.draft.ComposerDraft
import io.prism.android.libraries.prism.api.room.draft.ComposerDraftType
import org.prism.rustcomponents.sdk.ComposerDraft as RustComposerDraft
import org.prism.rustcomponents.sdk.ComposerDraftType as RustComposerDraftType

internal fun ComposerDraft.into(): RustComposerDraft {
    return RustComposerDraft(
        plainText = plainText,
        htmlText = htmlText,
        draftType = draftType.into(),
        // TODO add media attachments to the draft
        attachments = emptyList(),
    )
}

internal fun RustComposerDraft.into(): ComposerDraft {
    return ComposerDraft(
        plainText = plainText,
        htmlText = htmlText,
        draftType = draftType.into()
    )
}

private fun RustComposerDraftType.into(): ComposerDraftType {
    return when (this) {
        RustComposerDraftType.NewMessage -> ComposerDraftType.NewMessage
        is RustComposerDraftType.Reply -> ComposerDraftType.Reply(EventId(eventId))
        is RustComposerDraftType.Edit -> ComposerDraftType.Edit(EventId(eventId))
    }
}

private fun ComposerDraftType.into(): RustComposerDraftType {
    return when (this) {
        ComposerDraftType.NewMessage -> RustComposerDraftType.NewMessage
        is ComposerDraftType.Reply -> RustComposerDraftType.Reply(eventId.value)
        is ComposerDraftType.Edit -> RustComposerDraftType.Edit(eventId.value)
    }
}
