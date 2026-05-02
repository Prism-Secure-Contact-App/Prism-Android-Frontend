/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.voiceplayer.api

import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.media.MediaSource
import kotlin.time.Duration

interface VoiceMessagePresenterFactory {
    fun createVoiceMessagePresenter(
        eventId: EventId?,
        mediaSource: MediaSource,
        mimeType: String?,
        filename: String?,
        duration: Duration,
    ): Presenter<VoiceMessageState>
}
