/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.llmapi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.prism.android.features.preferences.impl.R
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.services.toolbox.api.strings.StringProvider
import java.security.SecureRandom

@Inject
class LlmApiSettingsPresenter(
    private val stringProvider: StringProvider,
) : Presenter<LlmApiSettingsState> {

    @Composable
    override fun present(): LlmApiSettingsState {
        var apiKey by remember { mutableStateOf<String?>(null) }
        var isRevealed by remember { mutableStateOf(false) }
        var snackbarMessage by remember { mutableStateOf<String?>(null) }
        val secureRandom = remember { SecureRandom() }

        // TODO(v1.1): Persist keys in encrypted KeyStore and sync with backend.
        // For v1.0.0 we generate client-side using SecureRandom and store only in memory.
        fun generateKey(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return "prism_sk_" + CharArray(32) { chars[secureRandom.nextInt(chars.length)] }.concatToString()
        }

        fun handleEvent(event: LlmApiSettingsEvents) {
            when (event) {
                is LlmApiSettingsEvents.GenerateApiKey -> {
                    apiKey = generateKey()
                    isRevealed = true
                    snackbarMessage = stringProvider.getString(R.string.screen_llm_api_settings_snackbar_generated)
                }
                is LlmApiSettingsEvents.RevealApiKey -> {
                    isRevealed = true
                }
                is LlmApiSettingsEvents.CopyApiKey -> {
                    snackbarMessage = stringProvider.getString(R.string.screen_llm_api_settings_snackbar_copied)
                }
                is LlmApiSettingsEvents.RotateApiKey -> {
                    apiKey = generateKey()
                    isRevealed = true
                    snackbarMessage = stringProvider.getString(R.string.screen_llm_api_settings_snackbar_rotated)
                }
                is LlmApiSettingsEvents.DeleteApiKey -> {
                    apiKey = null
                    isRevealed = false
                    snackbarMessage = stringProvider.getString(R.string.screen_llm_api_settings_snackbar_deleted)
                }
                is LlmApiSettingsEvents.DismissSnackbar -> {
                    snackbarMessage = null
                }
            }
        }

        return LlmApiSettingsState(
            apiKey = apiKey,
            isRevealed = isRevealed,
            snackbarMessage = snackbarMessage,
            eventSink = ::handleEvent,
        )
    }
}
