/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.changeserver

import io.prism.android.features.login.impl.accountprovider.AccountProvider

sealed interface ChangeServerEvents {
    data class ChangeServer(val accountProvider: AccountProvider) : ChangeServerEvents
    data object ClearError : ChangeServerEvents
}
