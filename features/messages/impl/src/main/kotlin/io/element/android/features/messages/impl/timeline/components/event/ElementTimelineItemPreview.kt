/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.theme.messageFromMeBackground

@Composable
internal fun PRISMTimelineItemPreview(
    content: @Composable BoxScope.() -> Unit,
) = PRISMPreview {
    Box(
        modifier = Modifier
            .background(PRISMTheme.colors.messageFromMeBackground)
            .padding(4.dp),
        content = content,
    )
}
