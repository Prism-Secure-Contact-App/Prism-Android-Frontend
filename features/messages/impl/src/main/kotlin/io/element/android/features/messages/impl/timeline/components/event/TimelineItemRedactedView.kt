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
import androidx.compose.ui.res.stringResource
import io.prism.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.prism.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.prism.android.libraries.designsystem.icons.CompoundDrawables
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun TimelineItemRedactedView(
    @Suppress("UNUSED_PARAMETER") content: TimelineItemRedactedContent,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier
) {
    TimelineItemInformativeView(
        text = stringResource(id = CommonStrings.common_message_removed),
        iconDescription = stringResource(id = CommonStrings.common_message_removed),
        iconResourceId = CompoundDrawables.ic_compound_delete,
        onContentLayoutChange = onContentLayoutChange,
        modifier = modifier
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemRedactedViewPreview() = PRISMPreview {
    TimelineItemRedactedView(
        TimelineItemRedactedContent,
        onContentLayoutChange = {},
    )
}
