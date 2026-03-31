/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api.timeline.item.event

import io.prism.android.libraries.prism.api.core.UserId

data class Receipt(
    val userId: UserId,
    val timestamp: Long,
)
