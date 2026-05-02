/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.voiceplayer.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.di.annotations.SessionCoroutineScope
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.media.MediaSource
import io.prism.android.libraries.voiceplayer.api.VoiceMessagePresenterFactory
import io.prism.android.libraries.voiceplayer.api.VoiceMessageState
import io.prism.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

@ContributesBinding(RoomScope::class)
class DefaultVoiceMessagePresenterFactory(
    private val analyticsService: AnalyticsService,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val voiceMessagePlayerFactory: VoiceMessagePlayer.Factory,
    private val voicePlayerStore: VoicePlayerStore,
) : VoiceMessagePresenterFactory {
    override fun createVoiceMessagePresenter(
        eventId: EventId?,
        mediaSource: MediaSource,
        mimeType: String?,
        filename: String?,
        duration: Duration,
    ): Presenter<VoiceMessageState> {
        val player = voiceMessagePlayerFactory.create(
            eventId = eventId,
            mediaSource = mediaSource,
            mimeType = mimeType,
            filename = filename,
        )

        return VoiceMessagePresenter(
            analyticsService = analyticsService,
            sessionCoroutineScope = sessionCoroutineScope,
            voicePlayerStore = voicePlayerStore,
            player = player,
            eventId = eventId,
            duration = duration,
        )
    }
}
