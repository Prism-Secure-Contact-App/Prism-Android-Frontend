/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.share

import io.prism.android.features.location.api.Location
import kotlin.time.Duration

sealed interface ShareLocationEvent {
    data class ShareStaticLocation(
        val location: Location,
        val isPinned: Boolean,
    ) : ShareLocationEvent

    data object ShowLiveLocationDurationPicker : ShareLocationEvent
    data class StartLiveLocationShare(val duration: Duration) : ShareLocationEvent

    data object StartTrackingUserLocation : ShareLocationEvent
    data object StopTrackingUserLocation : ShareLocationEvent
    data object DismissDialog : ShareLocationEvent

    data object RequestPermissions : ShareLocationEvent
    data object OpenAppSettings : ShareLocationEvent
    data object OpenLocationSettings : ShareLocationEvent
}
