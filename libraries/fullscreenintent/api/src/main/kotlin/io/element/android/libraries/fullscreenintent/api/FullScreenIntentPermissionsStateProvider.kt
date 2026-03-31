/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.fullscreenintent.api

fun aFullScreenIntentPermissionsState(
    permissionGranted: Boolean = true,
    shouldDisplay: Boolean = false,
    eventSink: (FullScreenIntentPermissionsEvents) -> Unit = {},
) = FullScreenIntentPermissionsState(
    permissionGranted = permissionGranted,
    shouldDisplayBanner = shouldDisplay,
    eventSink = eventSink,
)
