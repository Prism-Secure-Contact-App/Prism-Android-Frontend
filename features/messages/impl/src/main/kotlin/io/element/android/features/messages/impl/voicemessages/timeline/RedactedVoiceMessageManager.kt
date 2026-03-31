/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.voicemessages.timeline

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.timeline.PRISMTimelineItem
import io.prism.android.libraries.prism.api.timeline.item.event.RedactedContent
import io.prism.android.libraries.mediaplayer.api.MediaPlayer
import kotlinx.coroutines.withContext

interface RedactedVoiceMessageManager {
    suspend fun onEachPRISMTimelineItem(timelineItems: List<PRISMTimelineItem>)
}

@ContributesBinding(RoomScope::class)
class DefaultRedactedVoiceMessageManager(
    private val dispatchers: CoroutineDispatchers,
    private val mediaPlayer: MediaPlayer,
) : RedactedVoiceMessageManager {
    override suspend fun onEachPRISMTimelineItem(timelineItems: List<PRISMTimelineItem>) {
        withContext(dispatchers.computation) {
            mediaPlayer.state.value.let { playerState ->
                if (playerState.isPlaying && playerState.mediaId != null) {
                    val needsToPausePlayer = timelineItems.any {
                        it is PRISMTimelineItem.Event &&
                            playerState.mediaId == it.eventId?.value &&
                            it.event.content is RedactedContent
                    }
                    if (needsToPausePlayer) {
                        withContext(dispatchers.main) { mediaPlayer.pause() }
                    }
                }
            }
        }
    }
}
