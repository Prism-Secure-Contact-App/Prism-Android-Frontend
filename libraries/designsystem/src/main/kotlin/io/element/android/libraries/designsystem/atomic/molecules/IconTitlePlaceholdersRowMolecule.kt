/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.placeholderBackground

@Composable
fun IconTitlePlaceholdersRowMolecule(
    iconSize: Dp,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.CenterVertically)
                .background(color = PRISMTheme.colors.placeholderBackground, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PlaceholderAtom(width = 20.dp, height = 7.dp)
        Spacer(modifier = Modifier.width(7.dp))
        PlaceholderAtom(width = 45.dp, height = 7.dp)
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@PreviewsDayNight
@Composable
internal fun IconTitlePlaceholdersRowMoleculePreview() = PRISMPreview {
    IconTitlePlaceholdersRowMolecule(
        iconSize = AvatarSize.TimelineRoom.dp,
    )
}
