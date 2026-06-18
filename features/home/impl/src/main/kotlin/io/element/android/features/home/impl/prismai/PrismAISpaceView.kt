/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package io.prism.android.features.home.impl.prismai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import android.content.ClipData
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import io.prism.android.libraries.designsystem.components.list.ListItemContent
import io.prism.android.libraries.designsystem.theme.components.FloatingActionButton
import io.prism.android.libraries.designsystem.theme.components.HorizontalDivider
import io.prism.android.libraries.designsystem.theme.components.IconSource
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.theme.components.ListItem
import io.prism.android.libraries.designsystem.theme.components.Scaffold
import io.prism.android.libraries.designsystem.theme.components.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import io.prism.android.features.home.impl.R
import androidx.compose.material3.TextField
import io.prism.android.libraries.designsystem.theme.components.TopAppBar

@Composable
fun PrismAISpaceView(
    state: PrismAISpaceState,
    onRoomClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    when (state) {
        is PrismAISpaceState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopAppBar(title = { Text(stringResource(R.string.screen_prism_ai_title)) }, navigationIcon = { BackIcon(onBackClick) })
                Spacer(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.screen_prism_ai_loading))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        is PrismAISpaceState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopAppBar(title = { Text(stringResource(R.string.screen_prism_ai_title)) }, navigationIcon = { BackIcon(onBackClick) })
                Spacer(modifier = Modifier.weight(1f))
                Text(state.message)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        is PrismAISpaceState.Loaded -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = { Text(state.spaceName) },
                        navigationIcon = { BackIcon(onBackClick) },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { state.eventSink(PrismAISpaceEvents.GenerateApiKey) },
                    ) {
                        Icon(imageVector = CompoundIcons.Key(), contentDescription = stringResource(R.string.screen_prism_ai_a11y_generate_api_key))
                    }
                },
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    // ── Chat Section ──
                    item {
                        Text(
                            text = stringResource(R.string.screen_prism_ai_chat_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    if (state.chatMessages.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.screen_prism_ai_chat_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    } else {
                        items(state.chatMessages, key = { it.id }) { message ->
                            ChatBubble(message = message)
                        }
                    }

                    if (state.isSending) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.screen_prism_ai_typing), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    state.chatError?.let { error ->
                        item {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                        }
                    }

                    item {
                        ChatInputRow(
                            input = state.chatInput,
                            isSending = state.isSending,
                            onInputChange = { state.eventSink(PrismAISpaceEvents.UpdateChatInput(it)) },
                            onSend = {
                                state.eventSink(PrismAISpaceEvents.SendChatMessage(state.chatInput))
                            },
                        )
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                    // ── Rooms Section ──
                    item {
                        Text(
                            text = stringResource(R.string.screen_prism_ai_rooms_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    if (state.rooms.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(stringResource(R.string.screen_prism_ai_rooms_empty))
                                Text(stringResource(R.string.screen_prism_ai_rooms_hint))
                            }
                        }
                    } else {
                        items(state.rooms) { room ->
                            ListItem(
                                headlineContent = { Text(room.displayName) },
                                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Chat())),
                                onClick = {
                                    state.eventSink(PrismAISpaceEvents.RoomClick(room.roomId.value))
                                    onRoomClick(room.roomId.value)
                                },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            if (state.showGenerateKeyDialog && state.apiKey != null) {
                AlertDialog(
                    onDismissRequest = { state.eventSink(PrismAISpaceEvents.DismissKeyDialog) },
                    title = { Text(stringResource(R.string.screen_prism_ai_api_key_dialog_title)) },
                    text = {
                        Column {
                            Text(stringResource(R.string.screen_prism_ai_api_key_dialog_body))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.apiKey,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        scope.launch {
                                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("PRISM AI API key", state.apiKey)))
                                            state.eventSink(PrismAISpaceEvents.CopyApiKey)
                                        }
                                    },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.screen_prism_ai_api_key_dialog_tap_to_copy))
                        }
                    },
                    confirmButton = {
                        Text(
                            text = stringResource(R.string.screen_prism_ai_api_key_dialog_copy),
                            modifier = Modifier
                                .clickable {
                                    scope.launch {
                                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("PRISM AI API key", state.apiKey)))
                                        state.eventSink(PrismAISpaceEvents.CopyApiKey)
                                    }
                                    state.eventSink(PrismAISpaceEvents.DismissKeyDialog)
                                }
                                .padding(16.dp),
                        )
                    },
                    dismissButton = {
                        Text(
                            text = stringResource(R.string.screen_prism_ai_api_key_dialog_close),
                            modifier = Modifier
                                .clickable { state.eventSink(PrismAISpaceEvents.DismissKeyDialog) }
                                .padding(16.dp),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: PrismAIChatMessage) {
    val isUser = message.role == "user"
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val arrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = arrangement,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ChatInputRow(
    input: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.screen_prism_ai_input_placeholder)) },
            singleLine = false,
            maxLines = 4,
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            onClick = onSend,
            enabled = input.isNotBlank() && !isSending,
        ) {
            Icon(imageVector = CompoundIcons.Send(), contentDescription = stringResource(R.string.screen_prism_ai_a11y_send))
        }
    }
}

@Composable
private fun BackIcon(onClick: () -> Unit) {
    Icon(
        imageVector = CompoundIcons.ArrowLeft(),
        contentDescription = stringResource(R.string.screen_prism_ai_a11y_back),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}
