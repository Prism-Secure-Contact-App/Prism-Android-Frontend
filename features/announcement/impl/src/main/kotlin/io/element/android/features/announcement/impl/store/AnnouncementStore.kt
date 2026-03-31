/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.announcement.impl.store

import io.prism.android.features.announcement.api.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementStore {
    suspend fun setAnnouncementStatus(
        announcement: Announcement,
        status: AnnouncementStatus,
    )

    fun announcementStatusFlow(
        announcement: Announcement,
    ): Flow<AnnouncementStatus>

    suspend fun reset()
}
