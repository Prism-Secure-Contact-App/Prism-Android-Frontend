/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.ftue.impl.notifications

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.permissions.api.aPermissionsState

open class NotificationsOptInStateProvider : PreviewParameterProvider<NotificationsOptInState> {
    override val values: Sequence<NotificationsOptInState>
        get() = sequenceOf(
            aNotificationsOptInState(),
            // Add other states here
        )
}

fun aNotificationsOptInState() = NotificationsOptInState(
    notificationsPermissionState = aPermissionsState(showDialog = false),
    eventSink = {}
)
