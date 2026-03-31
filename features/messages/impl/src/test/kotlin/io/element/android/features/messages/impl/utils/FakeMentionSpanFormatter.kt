/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.utils

import io.prism.android.libraries.textcomposer.mentions.MentionSpanFormatter
import io.prism.android.libraries.textcomposer.mentions.MentionType

class FakeMentionSpanFormatter(
    private val formatLambda: (MentionType) -> CharSequence = { type -> type.toString() },
) : MentionSpanFormatter {
    override fun formatDisplayText(mentionType: MentionType): CharSequence {
        return formatLambda(mentionType)
    }
}
