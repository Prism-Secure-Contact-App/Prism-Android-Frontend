/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rageshake.api.detection

import io.prism.android.features.rageshake.api.preferences.RageshakePreferencesState

data class RageshakeDetectionState(
    val takeScreenshot: Boolean,
    val showDialog: Boolean,
    val isStarted: Boolean,
    val preferenceState: RageshakePreferencesState,
    val eventSink: (RageshakeDetectionEvent) -> Unit
)
