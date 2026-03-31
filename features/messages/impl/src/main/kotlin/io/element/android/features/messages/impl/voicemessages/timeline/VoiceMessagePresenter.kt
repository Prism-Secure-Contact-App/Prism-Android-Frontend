/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.voicemessages.timeline

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import io.prism.android.features.messages.impl.timeline.di.TimelineItemEventContentKey
import io.prism.android.features.messages.impl.timeline.di.TimelineItemPresenterFactory
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.voiceplayer.api.VoiceMessagePresenterFactory
import io.prism.android.libraries.voiceplayer.api.VoiceMessageState

@BindingContainer
@ContributesTo(RoomScope::class)
interface VoiceMessagePresenterModule {
    @Binds
    @IntoMap
    @TimelineItemEventContentKey(TimelineItemVoiceContent::class)
    fun bindVoiceMessagePresenterFactory(factory: VoiceMessagePresenter.Factory): TimelineItemPresenterFactory<*, *>
}

@AssistedInject
class VoiceMessagePresenter(
    voiceMessagePresenterFactory: VoiceMessagePresenterFactory,
    @Assisted private val content: TimelineItemVoiceContent,
) : Presenter<VoiceMessageState> {
    @AssistedFactory
    fun interface Factory : TimelineItemPresenterFactory<TimelineItemVoiceContent, VoiceMessageState> {
        override fun create(content: TimelineItemVoiceContent): VoiceMessagePresenter
    }

    private val presenter = voiceMessagePresenterFactory.createVoiceMessagePresenter(
        eventId = content.eventId,
        mediaSource = content.mediaSource,
        mimeType = content.mimeType,
        filename = content.filename,
        duration = content.duration,
    )

    @Composable
    override fun present(): VoiceMessageState {
        return presenter.present()
    }
}
