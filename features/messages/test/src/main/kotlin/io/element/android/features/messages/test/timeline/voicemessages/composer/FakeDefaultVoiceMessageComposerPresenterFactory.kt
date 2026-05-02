/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.test.timeline.voicemessages.composer

import io.prism.android.features.messages.impl.voicemessages.composer.DefaultVoiceMessageComposerPresenter
import io.prism.android.features.messages.impl.voicemessages.composer.VoiceMessageComposerPlayer
import io.prism.android.features.messages.test.FakeMessageComposerContext
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.libraries.matrix.test.room.FakeJoinedRoom
import io.prism.android.libraries.mediaplayer.test.FakeAudioFocus
import io.prism.android.libraries.mediaplayer.test.FakeMediaPlayer
import io.prism.android.libraries.mediaupload.api.MediaSender
import io.prism.android.libraries.mediaupload.impl.DefaultMediaSender
import io.prism.android.libraries.mediaupload.test.FakeMediaOptimizationConfigProvider
import io.prism.android.libraries.mediaupload.test.FakeMediaPreProcessor
import io.prism.android.libraries.permissions.test.FakePermissionsPresenterFactory
import io.prism.android.libraries.voicerecorder.test.FakeVoiceRecorder
import io.prism.android.services.analytics.test.FakeAnalyticsService
import kotlinx.coroutines.CoroutineScope

class FakeDefaultVoiceMessageComposerPresenterFactory(
    private val sessionCoroutineScope: CoroutineScope,
    private val mediaSender: MediaSender = DefaultMediaSender(
        preProcessor = FakeMediaPreProcessor(),
        room = FakeJoinedRoom(),
        timelineMode = Timeline.Mode.Live,
        mediaOptimizationConfigProvider = FakeMediaOptimizationConfigProvider(),
    ),
) : DefaultVoiceMessageComposerPresenter.Factory {
    override fun create(timelineMode: Timeline.Mode): DefaultVoiceMessageComposerPresenter {
        return DefaultVoiceMessageComposerPresenter(
            sessionCoroutineScope = sessionCoroutineScope,
            timelineMode = timelineMode,
            voiceRecorder = FakeVoiceRecorder(),
            analyticsService = FakeAnalyticsService(),
            audioFocus = FakeAudioFocus(
                requestAudioFocusResult = { _, _ -> },
                releaseAudioFocusResult = { },
            ),
            mediaSenderFactory = { mediaSender },
            player = VoiceMessageComposerPlayer(
                mediaPlayer = FakeMediaPlayer(),
                sessionCoroutineScope = sessionCoroutineScope,
            ),
            messageComposerContext = FakeMessageComposerContext(),
            permissionsPresenterFactory = FakePermissionsPresenterFactory(),
        )
    }
}
