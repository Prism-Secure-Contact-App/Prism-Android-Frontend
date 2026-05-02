/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.accountselect.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.matrix.ui.components.aMatrixUserList
import kotlinx.collections.immutable.toImmutableList

open class AccountSelectStateProvider : PreviewParameterProvider<AccountSelectState> {
    override val values: Sequence<AccountSelectState>
        get() = sequenceOf(
            anAccountSelectState(),
            anAccountSelectState(accounts = aMatrixUserList()),
        )
}

private fun anAccountSelectState(
    accounts: List<PRISMUser> = listOf(),
) = AccountSelectState(
    accounts = accounts.toImmutableList(),
)
