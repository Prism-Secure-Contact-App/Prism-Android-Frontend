/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.api.timeline.voicemessages.composer

import androidx.compose.runtime.Stable
import io.prism.android.libraries.textcomposer.model.VoiceMessageState

@Stable
data class VoiceMessageComposerState(
    val voiceMessageState: VoiceMessageState,
    val showPermissionRationaleDialog: Boolean,
    val showSendFailureDialog: Boolean,
    val keepScreenOn: Boolean,
    val eventSink: (VoiceMessageComposerEvent) -> Unit,
)
