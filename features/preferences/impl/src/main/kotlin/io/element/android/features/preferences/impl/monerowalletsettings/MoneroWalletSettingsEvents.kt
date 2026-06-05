/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

sealed interface MoneroWalletSettingsEvents {
    data object RevealSeedPhrase : MoneroWalletSettingsEvents
    data object CopySeedPhrase : MoneroWalletSettingsEvents
    data object CopyAddress : MoneroWalletSettingsEvents
    data object CopyViewKey : MoneroWalletSettingsEvents
    data object CopySpendKey : MoneroWalletSettingsEvents
    data object DismissSnackbar : MoneroWalletSettingsEvents
    data object ShowWithdrawDialog : MoneroWalletSettingsEvents
    data object DismissWithdrawDialog : MoneroWalletSettingsEvents
    data class SetWithdrawAddress(val address: String) : MoneroWalletSettingsEvents
    data class SetWithdrawAmount(val amount: String) : MoneroWalletSettingsEvents
    data object SubmitWithdraw : MoneroWalletSettingsEvents
}
