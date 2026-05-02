/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.textcomposer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.IconButton
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun VoiceMessageDeleteButtonIcon(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier.size(24.dp),
        imageVector = CompoundIcons.Delete(),
        contentDescription = stringResource(CommonStrings.a11y_delete),
        tint = if (enabled) {
            PRISMTheme.colors.iconCriticalPrimary
        } else {
            PRISMTheme.colors.iconDisabled
        },
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageDeleteButtonIconPreview() = PRISMPreview {
    Row {
        IconButton(onClick = {}) {
            VoiceMessageDeleteButtonIcon(
                enabled = true,
            )
        }
        IconButton(onClick = {}) {
            VoiceMessageDeleteButtonIcon(
                enabled = false,
            )
        }
    }
}
