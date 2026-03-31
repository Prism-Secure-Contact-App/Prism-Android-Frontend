/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.fullscreenintent.api

data class FullScreenIntentPermissionsState(
    val permissionGranted: Boolean,
    val shouldDisplayBanner: Boolean,
    val eventSink: (FullScreenIntentPermissionsEvents) -> Unit,
)
