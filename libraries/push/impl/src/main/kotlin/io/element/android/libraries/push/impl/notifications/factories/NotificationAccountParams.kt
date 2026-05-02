/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.push.impl.notifications.factories

import androidx.annotation.ColorInt
import io.prism.android.libraries.matrix.api.user.PRISMUser

data class NotificationAccountParams(
    val user: PRISMUser,
    @ColorInt val color: Int,
    val showSessionId: Boolean,
)
