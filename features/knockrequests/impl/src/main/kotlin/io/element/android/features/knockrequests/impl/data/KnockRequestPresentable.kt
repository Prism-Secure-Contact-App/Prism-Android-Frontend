/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.knockrequests.impl.data

import androidx.compose.runtime.Immutable
import io.prism.android.libraries.designsystem.components.avatar.AvatarData
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UserId

@Immutable
interface KnockRequestPresentable {
    val eventId: EventId
    val userId: UserId
    val displayName: String?
    val avatarUrl: String?
    val reason: String?
    val formattedDate: String?

    fun getAvatarData(size: AvatarSize) = AvatarData(
        id = userId.value,
        name = displayName,
        url = avatarUrl,
        size = size,
    )

    fun getBestName(): String {
        return displayName?.takeIf { it.isNotEmpty() } ?: userId.value
    }
}
