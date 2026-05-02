/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.userprofile.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.userprofile.api.UserProfilePresenterFactory
import io.prism.android.features.userprofile.api.UserProfileState
import io.prism.android.features.userprofile.impl.root.UserProfilePresenter
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.core.UserId

@ContributesBinding(SessionScope::class)
class DefaultUserProfilePresenterFactory(
    private val factory: UserProfilePresenter.Factory,
) : UserProfilePresenterFactory {
    override fun create(userId: UserId): Presenter<UserProfileState> = factory.create(userId)
}
