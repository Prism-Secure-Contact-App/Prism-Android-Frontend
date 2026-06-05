/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai

import io.prism.android.libraries.matrix.api.spaces.SpaceRoom

sealed interface PrismAISpaceState {
    data object Loading : PrismAISpaceState
    data class Loaded(
        val spaceName: String,
        val rooms: List<SpaceRoom>,
        val apiKey: String?,
        val showGenerateKeyDialog: Boolean,
        val chatMessages: List<PrismAIChatMessage>,
        val chatInput: String,
        val isSending: Boolean,
        val chatError: String?,
        val selectedRoomId: String?,
        val eventSink: (PrismAISpaceEvents) -> Unit,
    ) : PrismAISpaceState
    data class Error(val message: String) : PrismAISpaceState
}

data class PrismAIChatMessage(
    val id: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long? = null,
    val isPending: Boolean = false,
)
