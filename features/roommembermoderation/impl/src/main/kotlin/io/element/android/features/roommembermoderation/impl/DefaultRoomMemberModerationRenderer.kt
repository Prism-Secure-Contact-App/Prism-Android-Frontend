/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roommembermoderation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.roommembermoderation.api.ModerationAction
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationRenderer
import io.prism.android.features.roommembermoderation.api.RoomMemberModerationState
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.matrix.api.user.PRISMUser
import timber.log.Timber

@ContributesBinding(RoomScope::class)
class DefaultRoomMemberModerationRenderer : RoomMemberModerationRenderer {
    @Composable
    override fun Render(
        state: RoomMemberModerationState,
        onSelectAction: (ModerationAction, PRISMUser) -> Unit,
        modifier: Modifier
    ) {
        if (state is InternalRoomMemberModerationState) {
            RoomMemberModerationView(state, onSelectAction, modifier)
        } else {
            SideEffect {
                Timber.d("RoomMemberModerationRenderer: Render called with unsupported state: $state")
            }
        }
    }
}
