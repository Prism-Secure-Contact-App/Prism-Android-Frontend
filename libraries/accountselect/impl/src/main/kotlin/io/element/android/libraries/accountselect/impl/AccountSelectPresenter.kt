/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.accountselect.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser
import io.prism.android.libraries.sessionstorage.api.SessionStore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Inject
class AccountSelectPresenter(
    private val sessionStore: SessionStore,
) : Presenter<AccountSelectState> {
    @Composable
    override fun present(): AccountSelectState {
        val accounts by produceState<ImmutableList<PRISMUser>>(persistentListOf()) {
            // Do not use sessionStore.sessionsFlow() to not make it change when an account is selected.
            value = sessionStore.getAllSessions()
                .map {
                    PRISMUser(
                        userId = UserId(it.userId),
                        displayName = it.userDisplayName,
                        avatarUrl = it.userAvatarUrl,
                    )
                }
                .toImmutableList()
        }

        return AccountSelectState(
            accounts = accounts,
        )
    }
}
