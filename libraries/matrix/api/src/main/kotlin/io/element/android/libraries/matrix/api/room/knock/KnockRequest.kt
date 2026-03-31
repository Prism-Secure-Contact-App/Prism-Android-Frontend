/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.room.knock

import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.UserId

interface KnockRequest {
    val eventId: EventId
    val userId: UserId
    val displayName: String?
    val avatarUrl: String?
    val reason: String?
    val timestamp: Long?
    val isSeen: Boolean

    suspend fun accept(): Result<Unit>

    suspend fun decline(reason: String?): Result<Unit>

    suspend fun declineAndBan(reason: String?): Result<Unit>

    suspend fun markAsSeen(): Result<Unit>
}
