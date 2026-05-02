/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model

import io.prism.android.libraries.matrix.api.core.UserId
import io.prism.android.libraries.matrix.api.user.PRISMUser

data class AggregatedReactionSender(
    val senderId: UserId,
    val timestamp: Long,
    val sentTime: String,
    val user: PRISMUser? = null
)
