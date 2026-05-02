/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.recentemojis.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.recentemojis.api.EmojibaseProvider
import io.prism.android.libraries.recentemojis.api.GetRecentEmojis
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext

@ContributesBinding(SessionScope::class)
class DefaultGetRecentEmojis(
    private val client: PRISMClient,
    private val dispatchers: CoroutineDispatchers,
    private val emojibaseProvider: EmojibaseProvider,
) : GetRecentEmojis {
    override suspend operator fun invoke(): Result<ImmutableList<String>> = withContext(dispatchers.io) {
        val allEmojis = emojibaseProvider.emojibaseStore.allEmojis
        client.getRecentEmojis()
            .map { emojis ->
                // Remove any possible duplicates
                emojis.distinct()
                    // Return only those emojis that are valid
                    .filter { recent -> allEmojis.any { recent == it.unicode } }
                    .toImmutableList()
            }
    }
}
