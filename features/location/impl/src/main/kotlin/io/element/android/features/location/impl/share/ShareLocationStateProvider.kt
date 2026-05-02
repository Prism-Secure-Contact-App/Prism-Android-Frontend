/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.impl.share

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.features.location.impl.common.ui.LocationConstraintsDialogState
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private const val APP_NAME = "ApplicationName"

class ShareLocationStateProvider : PreviewParameterProvider<ShareLocationState> {
    override val values: Sequence<ShareLocationState>
        get() = sequenceOf(
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.None,
                trackUserPosition = false,
                hasLocationPermission = false,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.Constraints(LocationConstraintsDialogState.PermissionDenied),
                trackUserPosition = false,
                hasLocationPermission = false,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.Constraints(LocationConstraintsDialogState.PermissionRationale),
                trackUserPosition = false,
                hasLocationPermission = false,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.Constraints(LocationConstraintsDialogState.LocationServiceDisabled),
                trackUserPosition = false,
                hasLocationPermission = true,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.None,
                trackUserPosition = false,
                hasLocationPermission = true,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.None,
                trackUserPosition = true,
                hasLocationPermission = true,
            ),
            aShareLocationState(
                dialogState = ShareLocationState.Dialog.LiveLocationDurations(
                    persistentListOf(
                        LiveLocationDuration(15.minutes, "15 minutes"),
                        LiveLocationDuration(1.hours, "1 hour"),
                        LiveLocationDuration(8.hours, "8 hours"),
                    )
                ),
                trackUserPosition = true,
                hasLocationPermission = true,
                canShareLiveLocation = true,
            ),
        )
}

fun aShareLocationState(
    currentUser: PRISMUser = PRISMUser(UserId("@user:prism.org")),
    dialogState: ShareLocationState.Dialog = ShareLocationState.Dialog.None,
    trackUserPosition: Boolean = false,
    hasLocationPermission: Boolean = false,
    canShareLiveLocation: Boolean = false,
    appName: String = APP_NAME,
    eventSink: (ShareLocationEvent) -> Unit = {},
): ShareLocationState {
    return ShareLocationState(
        currentUser = currentUser,
        dialogState = dialogState,
        trackUserLocation = trackUserPosition,
        hasLocationPermission = hasLocationPermission,
        canShareLiveLocation = canShareLiveLocation,
        appName = appName,
        eventSink = eventSink
    )
}
