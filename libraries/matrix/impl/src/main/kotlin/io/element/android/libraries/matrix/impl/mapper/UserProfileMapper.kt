/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.mapper

import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.user.PRISMUser
import org.prism.rustcomponents.sdk.UserProfile

fun UserProfile.map() = PRISMUser(
    userId = UserId(userId),
    displayName = displayName,
    avatarUrl = avatarUrl,
)
