/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.announcement.impl.spaces

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.features.announcement.api.Announcement
import io.prism.android.features.announcement.impl.store.AnnouncementStatus
import io.prism.android.features.announcement.impl.store.AnnouncementStore
import io.prism.android.libraries.architecture.Presenter
import kotlinx.coroutines.launch

@Inject
class SpaceAnnouncementPresenter(
    private val announcementStore: AnnouncementStore,
) : Presenter<SpaceAnnouncementState> {
    @Composable
    override fun present(): SpaceAnnouncementState {
        val localCoroutineScope = rememberCoroutineScope()

        fun handleEvent(event: SpaceAnnouncementEvents) {
            when (event) {
                SpaceAnnouncementEvents.Continue -> localCoroutineScope.launch {
                    announcementStore.setAnnouncementStatus(Announcement.Space, AnnouncementStatus.Shown)
                }
            }
        }

        return SpaceAnnouncementState(
            eventSink = ::handleEvent,
        )
    }
}
