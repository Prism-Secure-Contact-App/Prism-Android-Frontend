/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.startchat.impl.userlist

import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.usersearch.api.UserRepository

interface UserListPresenter : Presenter<UserListState> {
    interface Factory {
        fun create(
            args: UserListPresenterArgs,
            userRepository: UserRepository,
            userListDataStore: UserListDataStore,
        ): UserListPresenter
    }
}
