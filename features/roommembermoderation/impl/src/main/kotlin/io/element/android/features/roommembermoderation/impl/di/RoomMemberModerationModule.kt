/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationState
import io.prism.android.features.roommembermoderation.impl.RoomMemberModerationPresenter
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.RoomScope

@ContributesTo(RoomScope::class)
@BindingContainer
interface RoomMemberModerationModule {
    @Binds
    fun bindRoomMemberModerationPresenter(presenter: RoomMemberModerationPresenter): Presenter<RoomMemberModerationState>
}
