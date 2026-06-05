/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.home.impl.prismai

sealed interface PrismAISpaceEvents {
    data class RoomClick(val roomId: String) : PrismAISpaceEvents
    data object GenerateApiKey : PrismAISpaceEvents
    data object CopyApiKey : PrismAISpaceEvents
    data object DismissKeyDialog : PrismAISpaceEvents
    data class SendChatMessage(val message: String) : PrismAISpaceEvents
    data class UpdateChatInput(val input: String) : PrismAISpaceEvents
    data class SelectRoomForChat(val roomId: String?) : PrismAISpaceEvents
    data object LoadChatHistory : PrismAISpaceEvents
}
