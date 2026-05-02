/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.knockrequests.impl.data

import io.prism.android.libraries.matrix.api.core.EventId
import io.prism.android.libraries.matrix.api.core.UserId

fun aKnockRequestPresentable(
    eventId: EventId = EventId("\$eventId"),
    userId: UserId = UserId("@jacob_ross:example.com"),
    displayName: String? = "Jacob Ross",
    avatarUrl: String? = null,
    reason: String? = "Hi, I would like to get access to this room please.",
    formattedDate: String? = "20 Nov 2024",
) = object : KnockRequestPresentable {
    override val eventId: EventId = eventId
    override val userId: UserId = userId
    override val displayName: String? = displayName
    override val avatarUrl: String? = avatarUrl
    override val reason: String? = reason
    override val formattedDate: String? = formattedDate
}
