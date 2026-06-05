/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.llmapi

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.components.preferences.PreferencePage
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.ListItemStyle
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.utils.snackbar.SnackbarHost

@Composable
fun LlmApiSettingsView(
    state: LlmApiSettingsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            state.eventSink(LlmApiSettingsEvents.DismissSnackbar)
        }
    }

    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = "LLM API",
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        if (state.apiKey == null) {
            ListItem(
                headlineContent = { Text("Generate API Key") },
                supportingContent = { Text("Generate an API key to use Meta AI on external platforms") },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
                onClick = { state.eventSink(LlmApiSettingsEvents.GenerateApiKey) },
            )
        } else {
            ListItem(
                headlineContent = { Text("API Key") },
                supportingContent = {
                    Text(
                        text = if (state.isRevealed) state.apiKey else "••••••••••••••••••••••••••",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    )
                },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
                onClick = { state.eventSink(LlmApiSettingsEvents.CopyApiKey) },
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Yenile (Rotate)") },
                supportingContent = { Text("Invalidate the current API key and generate a new one") },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Compose())),
                onClick = { state.eventSink(LlmApiSettingsEvents.RotateApiKey) },
            )

            ListItem(
                headlineContent = { Text("Sil") },
                supportingContent = { Text("Permanently delete the API key") },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Close())),
                style = ListItemStyle.Destructive,
                onClick = { state.eventSink(LlmApiSettingsEvents.DeleteApiKey) },
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "You can use this API key to access Meta AI on external platforms (similar to a Gemini API key).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
