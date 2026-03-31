/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.text.toDp
import io.prism.android.libraries.designsystem.theme.components.Text

private const val MAX_COUNT = 99
private const val MAX_COUNT_STRING = "+$MAX_COUNT"

/**
 * A counter atom that displays a number in a circle.
 * Figma link : https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=2805-2649&m=dev
 *
 * @param count The number to display. If the number is greater than [MAX_COUNT], the counter will display [MAX_COUNT_STRING].
 * If the number is less than 1, the counter will not be displayed.
 * @param modifier The modifier to apply to this layout.
 * @param textStyle The style to apply to the text inside the counter.
 * @param isCritical If true, the counter will use a critical color scheme, otherwise it will use an accent color scheme.
 */
@Composable
fun CounterAtom(
    count: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = CounterAtomDefaults.textStyle,
    isCritical: Boolean = false,
) {
    if (count < 1) return
    val countAsText = when (count) {
        in 0..MAX_COUNT -> count.toString()
        else -> MAX_COUNT_STRING
    }
    val textMeasurer = rememberTextMeasurer()
    // Measure the maximum count string size
    val textLayoutResult = textMeasurer.measure(
        text = MAX_COUNT_STRING,
        style = textStyle
    )
    val textSize = textLayoutResult.size
    val squareSize = maxOf(textSize.width, textSize.height)
    Box(
        modifier = modifier
            .size(squareSize.toDp() + 1.dp)
            .clip(CircleShape)
            .background(
                if (isCritical) {
                    PRISMTheme.colors.iconCriticalPrimary
                } else {
                    PRISMTheme.colors.iconAccentPrimary
                }
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = countAsText,
            style = textStyle,
            color = PRISMTheme.colors.textOnSolidPrimary,
        )
    }
}

object CounterAtomDefaults {
    val textStyle: TextStyle
        @Composable get() = PRISMTheme.typography.fontBodyMdMedium
}

@PreviewsDayNight
@Composable
internal fun CounterAtomPreview() = PRISMPreview {
    Column(verticalArrangement = spacedBy(2.dp)) {
        CounterAtom(count = 0)
        CounterAtom(count = 4)
        CounterAtom(count = 99)
        CounterAtom(count = 100)
        CounterAtom(count = 4, isCritical = true)
    }
}
