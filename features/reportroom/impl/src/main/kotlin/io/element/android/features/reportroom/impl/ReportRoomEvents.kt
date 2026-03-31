/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.reportroom.impl

sealed interface ReportRoomEvents {
    data class UpdateReason(val reason: String) : ReportRoomEvents
    data object ToggleLeaveRoom : ReportRoomEvents
    data object Report : ReportRoomEvents
    data object ClearReportAction : ReportRoomEvents
}
