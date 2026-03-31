/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.show

import io.prism.android.features.location.api.Location
import io.prism.android.features.location.impl.common.ui.LocationConstraintsDialogState
import io.prism.android.features.location.impl.common.ui.LocationMarkerData
import io.prism.android.libraries.designsystem.components.PinVariant
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.room.location.AssetType
import kotlinx.collections.immutable.ImmutableList

data class ShowLocationState(
    val dialogState: LocationConstraintsDialogState,
    val locationShares: ImmutableList<LocationShareItem>,
    val hasLocationPermission: Boolean,
    val isTrackMyLocation: Boolean,
    val appName: String,
    val eventSink: (ShowLocationEvent) -> Unit,
) {
    val isSheetDraggable = locationShares.any { item -> item.isLive }
}

data class LocationShareItem(
    val userId: UserId,
    val displayName: String,
    val avatarData: AvatarData,
    val formattedTimestamp: String,
    val location: Location,
    val isLive: Boolean,
    val assetType: AssetType?,
)

fun LocationShareItem.toMarkerData(): LocationMarkerData {
    val pinVariant = if (assetType == AssetType.PIN) {
        PinVariant.PinnedLocation
    } else {
        PinVariant.UserLocation(
            avatarData = avatarData,
            isLive = isLive,
        )
    }
    return LocationMarkerData(
        id = userId.value,
        location = location,
        variant = pinVariant,
    )
}
