/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.chooseaccountprovider

import io.prism.android.features.login.impl.accountprovider.AccountProvider

sealed interface ChooseAccountProviderEvents {
    data class SelectAccountProvider(val accountProvider: AccountProvider) : ChooseAccountProviderEvents
    data object Continue : ChooseAccountProviderEvents
    data object ClearError : ChooseAccountProviderEvents
}
