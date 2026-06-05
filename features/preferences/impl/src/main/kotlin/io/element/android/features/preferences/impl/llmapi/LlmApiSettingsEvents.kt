/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.llmapi

sealed interface LlmApiSettingsEvents {
    data object GenerateApiKey : LlmApiSettingsEvents
    data object RevealApiKey : LlmApiSettingsEvents
    data object CopyApiKey : LlmApiSettingsEvents
    data object RotateApiKey : LlmApiSettingsEvents
    data object DeleteApiKey : LlmApiSettingsEvents
    data object DismissSnackbar : LlmApiSettingsEvents
}
