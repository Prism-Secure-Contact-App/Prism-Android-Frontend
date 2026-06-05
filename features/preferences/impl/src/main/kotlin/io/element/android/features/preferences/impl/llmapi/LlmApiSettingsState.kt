/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.llmapi

data class LlmApiSettingsState(
    val apiKey: String?,
    val isRevealed: Boolean,
    val snackbarMessage: String?,
    val eventSink: (LlmApiSettingsEvents) -> Unit,
)
