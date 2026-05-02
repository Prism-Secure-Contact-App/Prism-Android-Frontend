/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.prism.android.features.invite.api.SeenInvitesStore
import io.prism.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.prism.android.features.invite.impl.SeenInvitesStoreFactory
import io.prism.android.features.invite.impl.acceptdecline.AcceptDeclineInvitePresenter
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient

@ContributesTo(SessionScope::class)
@BindingContainer
interface InviteModule {
    @Binds
    fun bindAcceptDeclinePresenter(presenter: AcceptDeclineInvitePresenter): Presenter<AcceptDeclineInviteState>

    companion object {
        @Provides
        fun providesSeenInvitesStore(
            factory: SeenInvitesStoreFactory,
            matrixClient: PRISMClient,
        ): SeenInvitesStore {
            return factory.getOrCreate(
                matrixClient.sessionId,
                matrixClient.sessionCoroutineScope,
            )
        }
    }
}
