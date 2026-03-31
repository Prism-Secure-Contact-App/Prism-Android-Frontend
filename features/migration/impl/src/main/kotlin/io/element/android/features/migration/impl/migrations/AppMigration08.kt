/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.migration.impl.migrations

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.prism.android.features.announcement.api.Announcement
import io.prism.android.features.announcement.api.AnnouncementService

/**
 * Ensure the new notification sound banner is displayed, but only on application upgrade.
 */
@ContributesIntoSet(AppScope::class)
class AppMigration08(
    private val announcementService: AnnouncementService,
) : AppMigration {
    override val order: Int = 8

    override suspend fun migrate(isFreshInstall: Boolean) {
        if (!isFreshInstall) {
            announcementService.showAnnouncement(Announcement.NewNotificationSound)
        }
    }
}
