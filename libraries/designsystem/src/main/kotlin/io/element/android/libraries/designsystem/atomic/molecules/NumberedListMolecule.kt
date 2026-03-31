/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.modifiers.squareSize
import io.prism.android.libraries.designsystem.theme.components.Text

@Composable
fun NumberedListMolecule(
    index: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ItemNumber(index = index)
        Text(text = text, style = PRISMTheme.typography.fontBodyMdRegular, color = PRISMTheme.colors.textPrimary)
    }
}

@Composable
private fun ItemNumber(
    index: Int,
) {
    val color = PRISMTheme.colors.textSecondary
    Box(
        modifier = Modifier
            .border(1.dp, color, CircleShape)
            .squareSize()
    ) {
        Text(
            modifier = Modifier.padding(1.5.dp),
            text = index.toString(),
            style = PRISMTheme.typography.fontBodySmRegular,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}
