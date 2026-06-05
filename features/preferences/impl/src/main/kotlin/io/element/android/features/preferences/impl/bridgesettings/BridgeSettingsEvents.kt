/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.bridgesettings

sealed interface BridgeSettingsEvents {
    data class ConnectBridge(val platform: String) : BridgeSettingsEvents
    data class DisconnectBridge(val platform: String) : BridgeSettingsEvents
    data class SubmitPhone(val phone: String) : BridgeSettingsEvents
    data object RequestNewCode : BridgeSettingsEvents
    data object DismissSnackbar : BridgeSettingsEvents
    data object DismissDialog : BridgeSettingsEvents
}
