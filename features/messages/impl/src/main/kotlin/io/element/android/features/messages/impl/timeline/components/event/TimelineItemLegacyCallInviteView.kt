/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.timeline.components.event

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.messages.impl.R
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
fun TimelineItemLegacyCallInviteView(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Icon(
            imageVector = CompoundIcons.VoiceCallSolid(),
            contentDescription = null,
            tint = PRISMTheme.colors.iconSecondary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            color = PRISMTheme.colors.textSecondary,
            style = PRISMTheme.typography.fontBodyMdRegular,
            text = stringResource(R.string.screen_room_timeline_legacy_call),
            textAlign = TextAlign.Start,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineItemLegacyCallInviteViewPreview() = PRISMPreview {
    TimelineItemLegacyCallInviteView()
}
