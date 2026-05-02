/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.room.draft

/**
 * A draft of a message composed by the user.
 * @param plainText The draft content in plain text.
 * @param htmlText If the message is formatted in HTML, the HTML representation of the message.
 * @param draftType The type of draft.
 */
data class ComposerDraft(
    val plainText: String,
    val htmlText: String?,
    val draftType: ComposerDraftType
)
