/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemAudioContentProvider
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight

@Composable
fun TimelineItemAudioView(
    content: TimelineItemAudioContent,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier,
) {
    TimelineItemAttachmentView(
        icon = CompoundIcons.Audio(),
        iconContentDescription = null,
        filename = content.filename,
        fileExtensionAndSize = content.fileExtensionAndSize,
        caption = content.caption,
        onContentLayoutChange = onContentLayoutChange,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemAudioViewPreview(@PreviewParameter(TimelineItemAudioContentProvider::class) content: TimelineItemAudioContent) =
    PRISMTimelineItemPreview {
        TimelineItemAudioView(
            content,
            onContentLayoutChange = {},
        )
    }
