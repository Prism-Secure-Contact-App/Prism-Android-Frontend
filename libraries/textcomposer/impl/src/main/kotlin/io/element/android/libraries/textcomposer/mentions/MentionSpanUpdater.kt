/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.textcomposer.mentions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.matrix.ui.messages.RoomMemberProfilesCache
import io.prism.android.libraries.matrix.ui.messages.RoomNamesCache

interface MentionSpanUpdater {
    fun updateMentionSpans(text: CharSequence): CharSequence

    @Composable
    fun rememberMentionSpans(text: CharSequence): CharSequence
}

@ContributesBinding(RoomScope::class)
class DefaultMentionSpanUpdater(
    private val formatter: MentionSpanFormatter,
    private val theme: MentionSpanTheme,
    private val roomMemberProfilesCache: RoomMemberProfilesCache,
    private val roomNamesCache: RoomNamesCache,
) : MentionSpanUpdater {
    @Composable
    override fun rememberMentionSpans(text: CharSequence): CharSequence {
        val isLightTheme = PRISMTheme.isLightTheme
        val roomInfoCacheUpdate by roomNamesCache.updateFlow.collectAsState(0)
        val roomMemberProfilesCacheUpdate by roomMemberProfilesCache.updateFlow.collectAsState(0)
        return remember(text, roomInfoCacheUpdate, roomMemberProfilesCacheUpdate, isLightTheme) {
            updateMentionSpans(text)
            text
        }
    }

    override fun updateMentionSpans(text: CharSequence): CharSequence {
        for (mentionSpan in text.getMentionSpans()) {
            mentionSpan.updateTheme(theme)
            mentionSpan.updateDisplayText(formatter)
        }
        return text
    }
}

private object NoOpMentionSpanUpdater : MentionSpanUpdater {
    override fun updateMentionSpans(text: CharSequence): CharSequence {
        return text
    }

    @Composable
    override fun rememberMentionSpans(text: CharSequence): CharSequence {
        return text
    }
}

val LocalMentionSpanUpdater = staticCompositionLocalOf<MentionSpanUpdater> { NoOpMentionSpanUpdater }
