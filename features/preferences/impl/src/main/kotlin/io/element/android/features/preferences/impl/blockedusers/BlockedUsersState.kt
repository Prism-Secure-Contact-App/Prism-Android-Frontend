/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.blockedusers

import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.matrix.api.user.PRISMUser
import kotlinx.collections.immutable.ImmutableList

data class BlockedUsersState(
    val blockedUsers: ImmutableList<PRISMUser>,
    val unblockUserAction: AsyncAction<Unit>,
    val eventSink: (BlockedUsersEvents) -> Unit,
)
