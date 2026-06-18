/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.bridgesettings

import io.prism.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList

data class BridgeInfo(
    val platform: String,
    val displayName: String,
    val botUserId: String,
    val isActive: Boolean,
    val roomId: RoomId?,
    val identifier: String? = null,
)

data class BridgeSettingsState(
    val bridges: ImmutableList<BridgeInfo>,
    val isLoading: Boolean,
    val snackbarMessage: String?,
    val dialog: BridgeDialog?,
    val eventSink: (BridgeSettingsEvents) -> Unit,
)

sealed interface BridgeDialog {
    data class PhoneInput(
        val platform: String,
        val prompt: String = "",
        val inputLabel: String = "",
        val placeholder: String = "",
        val initialValue: String = "+",
    ) : BridgeDialog

    data class PairingCode(
        val platform: String,
        val code: String,
        val caption: String,
    ) : BridgeDialog

    data class Error(val message: String) : BridgeDialog
}
