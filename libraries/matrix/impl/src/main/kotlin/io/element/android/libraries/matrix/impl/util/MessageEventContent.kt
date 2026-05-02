/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.util

import io.prism.android.libraries.matrix.api.room.IntentionalMention
import io.prism.android.libraries.matrix.impl.room.map
import org.matrix.rustcomponents.sdk.RoomMessageEventContentWithoutRelation
import org.matrix.rustcomponents.sdk.messageEventContentFromHtml
import org.matrix.rustcomponents.sdk.messageEventContentFromMarkdown

/**
 * Creates a [RoomMessageEventContentWithoutRelation] from a body, an html body and a list of mentions.
 */
object MessageEventContent {
    fun from(body: String, htmlBody: String?, intentionalMentions: List<IntentionalMention>): RoomMessageEventContentWithoutRelation {
        return if (htmlBody != null) {
            messageEventContentFromHtml(body, htmlBody)
        } else {
            messageEventContentFromMarkdown(body)
        }.withMentions(intentionalMentions.map())
    }
}
