/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invitepeople.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface InvitePeopleRenderer {
    @Composable
    fun Render(
        state: InvitePeopleState,
        modifier: Modifier,
    )
}
