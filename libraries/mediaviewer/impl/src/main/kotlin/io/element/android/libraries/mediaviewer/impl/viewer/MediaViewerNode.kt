/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.mediaviewer.impl.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.compound.colors.SemanticColorsLightDark
import io.prism.android.compound.theme.ForcedDarkElementTheme
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.viewfolder.api.TextFileViewer
import io.prism.android.libraries.architecture.callback
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.audio.api.AudioFocus
import io.prism.android.libraries.core.coroutine.CoroutineDispatchers
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.api.media.PRISMMediaLoader
import io.prism.android.libraries.matrix.api.timeline.Timeline
import io.prism.android.libraries.mediaviewer.api.MediaViewerEntryPoint
import io.prism.android.libraries.mediaviewer.api.local.LocalMediaFactory
import io.prism.android.libraries.mediaviewer.impl.datasource.FocusedTimelineMediaGalleryDataSourceFactory
import io.prism.android.libraries.mediaviewer.impl.datasource.TimelineMediaGalleryDataSource
import io.prism.android.libraries.mediaviewer.impl.model.hasEvent
import io.prism.android.services.toolbox.api.systemclock.SystemClock

@ContributesNode(RoomScope::class)
@AssistedInject
class MediaViewerNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: MediaViewerPresenter.Factory,
    timelineMediaGalleryDataSource: TimelineMediaGalleryDataSource,
    focusedTimelineMediaGalleryDataSourceFactory: FocusedTimelineMediaGalleryDataSourceFactory,
    mediaLoader: PRISMMediaLoader,
    localMediaFactory: LocalMediaFactory,
    coroutineDispatchers: CoroutineDispatchers,
    systemClock: SystemClock,
    pagerKeysHandler: PagerKeysHandler,
    private val textFileViewer: TextFileViewer,
    private val audioFocus: AudioFocus,
    private val sessionId: SessionId,
    private val enterpriseService: EnterpriseService,
) : Node(buildContext, plugins = plugins),
    MediaViewerNavigator {
    private val callback: MediaViewerEntryPoint.Callback = callback()
    private val inputs = inputs<MediaViewerEntryPoint.Params>()

    override fun onViewInTimelineClick(eventId: EventId) {
        callback.viewInTimeline(eventId)
    }

    override fun onForwardClick(eventId: EventId, fromPinnedEvents: Boolean) {
        callback.forwardEvent(eventId, fromPinnedEvents)
    }

    override fun onItemDeleted() {
        callback.onDone()
    }

    private val mediaGallerySource = if (inputs.mode == MediaViewerEntryPoint.MediaViewerMode.SingleMedia) {
        SingleMediaGalleryDataSource.createFrom(inputs)
    } else {
        val eventId = inputs.eventId
        if (eventId == null) {
            // Should not happen
            timelineMediaGalleryDataSource
        } else {
            // Can we use a specific timeline?
            val timelineMode = inputs.mode.getTimelineMode()
            when (timelineMode) {
                null -> timelineMediaGalleryDataSource
                Timeline.Mode.Live,
                is Timeline.Mode.FocusedOnEvent,
                is Timeline.Mode.Thread -> {
                    // Does timelineMediaGalleryDataSource knows the eventId?
                    val lastData = timelineMediaGalleryDataSource.getLastData().dataOrNull()
                    val isEventKnown = lastData?.hasEvent(eventId) == true
                    if (isEventKnown) {
                        timelineMediaGalleryDataSource
                    } else {
                        focusedTimelineMediaGalleryDataSourceFactory.createFor(
                            eventId = eventId,
                            mediaItem = inputs.toMediaItem(),
                            onlyPinnedEvents = false,
                        )
                    }
                }
                Timeline.Mode.PinnedEvents -> {
                    focusedTimelineMediaGalleryDataSourceFactory.createFor(
                        eventId = eventId,
                        mediaItem = inputs.toMediaItem(),
                        onlyPinnedEvents = true,
                    )
                }
                Timeline.Mode.Media -> timelineMediaGalleryDataSource
            }
        }
    }

    private val presenter = presenterFactory.create(
        inputs = inputs,
        navigator = this,
        dataSource = MediaViewerDataSource(
            mode = inputs.mode,
            dispatcher = coroutineDispatchers.computation,
            galleryDataSource = mediaGallerySource,
            mediaLoader = mediaLoader,
            localMediaFactory = localMediaFactory,
            systemClock = systemClock,
            pagerKeysHandler = pagerKeysHandler,
        )
    )

    @Composable
    override fun View(modifier: Modifier) {
        val colors by remember {
            enterpriseService.semanticColorsFlow(sessionId = sessionId)
        }.collectAsState(SemanticColorsLightDark.default)
        ForcedDarkElementTheme(
            colors = colors,
        ) {
            val state = presenter.present()
            MediaViewerView(
                state = state,
                textFileViewer = textFileViewer,
                modifier = modifier,
                audioFocus = audioFocus,
                onBackClick = callback::onDone,
            )
        }
    }
}

internal fun MediaViewerEntryPoint.MediaViewerMode.getTimelineMode(): Timeline.Mode? {
    return when (this) {
        is MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos -> timelineMode
        is MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios -> timelineMode
        else -> null
    }
}
