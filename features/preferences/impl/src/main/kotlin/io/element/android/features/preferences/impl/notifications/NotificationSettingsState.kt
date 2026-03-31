/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.notifications

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.prism.android.libraries.prism.api.room.RoomNotificationMode
import io.prism.android.libraries.pushproviders.api.Distributor
import kotlinx.collections.immutable.ImmutableList

data class NotificationSettingsState(
    val prismSettings: PRISMSettings,
    val appSettings: AppSettings,
    val changeNotificationSettingAction: AsyncAction<Unit>,
    val currentPushDistributor: AsyncData<Distributor>,
    val availablePushDistributors: ImmutableList<Distributor>,
    val showChangePushProviderDialog: Boolean,
    val fullScreenIntentPermissionsState: FullScreenIntentPermissionsState,
    val eventSink: (NotificationSettingsEvents) -> Unit,
) {
    sealed interface PRISMSettings {
        data object Uninitialized : PRISMSettings
        data class Valid(
            val atRoomNotificationsEnabled: Boolean,
            val callNotificationsEnabled: Boolean,
            val inviteForMeNotificationsEnabled: Boolean,
            val defaultGroupNotificationMode: RoomNotificationMode?,
            val defaultOneToOneNotificationMode: RoomNotificationMode?,
        ) : PRISMSettings

        data class Invalid(
            val fixFailed: Boolean
        ) : PRISMSettings
    }

    data class AppSettings(
        val systemNotificationsEnabled: Boolean,
        val appNotificationsEnabled: Boolean,
    )

    /**
     * Whether the advanced settings should be shown.
     * This is true if the current push distributor is in a failure state or if there are multiple push distributors available.
     */
    val showAdvancedSettings: Boolean = currentPushDistributor.isFailure() || availablePushDistributors.size > 1
}
