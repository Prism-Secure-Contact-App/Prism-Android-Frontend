/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.virtual

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemDaySeparatorModel
import io.prism.android.features.messages.impl.timeline.model.virtual.TimelineItemDaySeparatorModelProvider
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
internal fun TimelineItemDaySeparatorView(
    model: TimelineItemDaySeparatorModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .semantics {
                    heading()
                },
            text = model.formattedDate,
            style = PRISMTheme.typography.fontBodyMdMedium,
            color = PRISMTheme.colors.textPrimary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineItemDaySeparatorViewPreview(
    @PreviewParameter(TimelineItemDaySeparatorModelProvider::class) model: TimelineItemDaySeparatorModel
) = PRISMPreview {
    TimelineItemDaySeparatorView(
        model = model,
    )
}
