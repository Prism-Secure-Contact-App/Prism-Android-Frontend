/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.share

import io.prism.android.features.location.impl.common.ui.LocationConstraintsDialogState
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class ShareLocationState(
    val currentUser: PRISMUser,
    val dialogState: Dialog,
    val trackUserLocation: Boolean,
    val hasLocationPermission: Boolean,
    val appName: String,
    val canShareLiveLocation: Boolean,
    val eventSink: (ShareLocationEvent) -> Unit,
) {
    sealed interface Dialog {
        data object None : Dialog
        data class Constraints(val state: LocationConstraintsDialogState) : Dialog
        data class LiveLocationDurations(val durations: ImmutableList<LiveLocationDuration>) : Dialog
    }
}
