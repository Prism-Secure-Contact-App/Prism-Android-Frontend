/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.textcomposer.impl.mentions

import io.prism.android.libraries.matrix.api.permalink.PermalinkParser
import io.prism.android.libraries.matrix.test.A_USER_ID
import io.prism.android.libraries.matrix.test.permalink.FakePermalinkParser
import io.prism.android.libraries.textcomposer.mentions.MentionSpanFormatter
import io.prism.android.libraries.textcomposer.mentions.MentionSpanProvider
import io.prism.android.libraries.textcomposer.mentions.MentionSpanTheme
import io.prism.android.libraries.textcomposer.mentions.MentionType

fun aMentionSpanProvider(
    permalinkParser: PermalinkParser = FakePermalinkParser(),
    mentionSpanFormatter: MentionSpanFormatter = object : MentionSpanFormatter {
        override fun formatDisplayText(mentionType: MentionType): CharSequence {
            return mentionType.toString()
        }
    },
    mentionSpanTheme: MentionSpanTheme = MentionSpanTheme(A_USER_ID),
): MentionSpanProvider {
    return MentionSpanProvider(
        permalinkParser = permalinkParser,
        mentionSpanFormatter = mentionSpanFormatter,
        mentionSpanTheme = mentionSpanTheme,
    )
}
