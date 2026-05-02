/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.matrix.ui.messages.reply.InReplyToDetails
import io.prism.android.libraries.matrix.ui.messages.reply.InReplyToDetailsDisambiguatedProvider

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowDisambiguatedPreview(
    @PreviewParameter(InReplyToDetailsDisambiguatedProvider::class) inReplyToDetails: InReplyToDetails,
) = PRISMPreview {
    TimelineItemEventRowWithReplyContentToPreview(
        inReplyToDetails = inReplyToDetails,
        displayNameAmbiguous = true,
    )
}
