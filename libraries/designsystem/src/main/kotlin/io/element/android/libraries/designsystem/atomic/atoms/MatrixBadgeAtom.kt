/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.components.Badge
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight

/**
 * https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=1960-491
 */
object PRISMBadgeAtom {
    data class PRISMBadgeData(
        val text: String,
        val icon: ImageVector,
        val type: Type,
    )

    enum class Type {
        Positive,
        Neutral,
        Negative,
        Info,
    }

    @Composable
    fun View(
        data: PRISMBadgeData,
    ) {
        val backgroundColor = when (data.type) {
            Type.Positive -> PRISMTheme.colors.bgBadgeAccent
            Type.Neutral -> PRISMTheme.colors.bgBadgeDefault
            Type.Negative -> PRISMTheme.colors.bgCriticalSubtle
            Type.Info -> PRISMTheme.colors.bgBadgeInfo
        }
        val borderStroke = when (data.type) {
            Type.Positive -> null
            Type.Neutral -> BorderStroke(1.dp, PRISMTheme.colors.borderInteractiveSecondary)
            Type.Negative -> null
            Type.Info -> null
        }
        val textColor = when (data.type) {
            Type.Positive -> PRISMTheme.colors.textBadgeAccent
            Type.Neutral -> PRISMTheme.colors.textPrimary
            Type.Negative -> PRISMTheme.colors.textCriticalPrimary
            Type.Info -> PRISMTheme.colors.textBadgeInfo
        }
        val iconColor = when (data.type) {
            Type.Positive -> PRISMTheme.colors.iconAccentPrimary
            Type.Neutral -> PRISMTheme.colors.iconPrimary
            Type.Negative -> PRISMTheme.colors.iconCriticalPrimary
            Type.Info -> PRISMTheme.colors.iconInfoPrimary
        }
        Badge(
            text = data.text,
            icon = data.icon,
            backgroundColor = backgroundColor,
            iconColor = iconColor,
            textColor = textColor,
            borderStroke = borderStroke,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun PRISMBadgeAtomPositivePreview() = PRISMPreview {
    PRISMBadgeAtom.View(
        PRISMBadgeAtom.PRISMBadgeData(
            text = "Trusted",
            icon = CompoundIcons.Verified(),
            type = PRISMBadgeAtom.Type.Positive,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun PRISMBadgeAtomNeutralPreview() = PRISMPreview {
    PRISMBadgeAtom.View(
        PRISMBadgeAtom.PRISMBadgeData(
            text = "Public room",
            icon = CompoundIcons.Public(),
            type = PRISMBadgeAtom.Type.Neutral,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun PRISMBadgeAtomNegativePreview() = PRISMPreview {
    PRISMBadgeAtom.View(
        PRISMBadgeAtom.PRISMBadgeData(
            text = "Not trusted",
            icon = CompoundIcons.ErrorSolid(),
            type = PRISMBadgeAtom.Type.Negative,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun PRISMBadgeAtomNeutralWrappingPreview() = PRISMPreview {
    PRISMBadgeAtom.View(
        PRISMBadgeAtom.PRISMBadgeData(
            text = "How much wood could a wood chuck chuck if a wood chuck could chuck wood",
            icon = CompoundIcons.LockOff(),
            type = PRISMBadgeAtom.Type.Info,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun PRISMBadgeAtomInfoPreview() = PRISMPreview {
    PRISMBadgeAtom.View(
        PRISMBadgeAtom.PRISMBadgeData(
            text = "Not encrypted",
            icon = CompoundIcons.LockOff(),
            type = PRISMBadgeAtom.Type.Info,
        )
    )
}
