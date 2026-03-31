/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.desktop

import io.prism.android.libraries.permissions.api.PermissionsState

data class DesktopNoticeState(
    val cameraPermissionState: PermissionsState,
    val canContinue: Boolean,
    val eventSink: (DesktopNoticeEvent) -> Unit,
)
