/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.model.event

import io.prism.android.libraries.prism.api.timeline.item.event.UnableToDecryptContent

data class TimelineItemEncryptedContent(
    val data: UnableToDecryptContent.Data
) : TimelineItemEventContent {
    override val type: String = "TimelineItemEncryptedContent"
}
