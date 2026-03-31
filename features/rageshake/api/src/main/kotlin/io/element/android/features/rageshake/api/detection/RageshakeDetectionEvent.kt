/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rageshake.api.detection

import io.prism.android.features.rageshake.api.screenshot.ImageResult

sealed interface RageshakeDetectionEvent {
    data object Dismiss : RageshakeDetectionEvent
    data object Disable : RageshakeDetectionEvent
    data object StartDetection : RageshakeDetectionEvent
    data object StopDetection : RageshakeDetectionEvent
    data class ProcessScreenshot(val imageResult: ImageResult) : RageshakeDetectionEvent
}
