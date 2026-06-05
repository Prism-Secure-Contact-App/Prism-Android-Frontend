/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.monerowalletsettings

import io.prism.android.libraries.architecture.AsyncAction

data class MoneroWalletSettingsState(
    val address: String?,
    val mnemonic: String?,
    val viewKey: String?,
    val spendKey: String?,
    val balance: String,
    val feeRate: String,
    val isRevealed: Boolean,
    val snackbarMessage: String?,
    val showWithdrawDialog: Boolean,
    val withdrawAddress: String,
    val withdrawAmount: String,
    val withdrawAction: AsyncAction<Unit>,
    val eventSink: (MoneroWalletSettingsEvents) -> Unit,
)
