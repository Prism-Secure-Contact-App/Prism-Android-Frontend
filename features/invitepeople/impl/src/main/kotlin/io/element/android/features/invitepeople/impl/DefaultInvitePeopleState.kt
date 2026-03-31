/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.TextFieldState
import io.prism.android.features.invitepeople.api.InvitePeopleEvents
import io.prism.android.features.invitepeople.api.InvitePeopleState
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.theme.components.SearchBarResultState
import io.prism.android.libraries.prism.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class DefaultInvitePeopleState(
    val room: AsyncData<Unit>,
    override val canInvite: Boolean,
    val searchQuery: TextFieldState,
    val showSearchLoader: Boolean,
    val searchResults: SearchBarResultState<ImmutableList<InvitableUser>>,
    val selectedUsers: ImmutableList<PRISMUser>,
    override val isSearchActive: Boolean,
    override val sendInvitesAction: AsyncAction<Unit>,
    val suggestions: ImmutableList<InvitableUser>,
    override val eventSink: (InvitePeopleEvents) -> Unit
) : InvitePeopleState
