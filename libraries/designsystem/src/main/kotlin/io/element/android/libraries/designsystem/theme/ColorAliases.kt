/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.prism.android.compound.annotations.CoreColorToken
import io.prism.android.compound.previews.ColorListPreview
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.SemanticColors
import io.prism.android.compound.tokens.generated.internal.DarkColorTokens
import io.prism.android.compound.tokens.generated.internal.LightColorTokens
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import kotlinx.collections.immutable.persistentMapOf

/**
 * Room list.
 */
val SemanticColors.roomListRoomName
    get() = textPrimary

val SemanticColors.roomListRoomMessage
    get() = textSecondary

val SemanticColors.roomListRoomMessageDate
    get() = textSecondary

val SemanticColors.unreadIndicator
    get() = iconAccentTertiary

val SemanticColors.placeholderBackground
    get() = bgSubtleSecondary

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.messageFromMeBackground
    get() = if (isLight) LightColorTokens.colorGray400 else DarkColorTokens.colorGray500

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.messageFromOtherBackground
    get() = if (isLight) LightColorTokens.colorGray300 else DarkColorTokens.colorGray400

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.progressIndicatorTrackColor
    get() = if (isLight) LightColorTokens.colorAlphaGray500 else DarkColorTokens.colorAlphaGray500

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.bgSubtleTertiary
    get() = if (isLight) LightColorTokens.colorGray100 else DarkColorTokens.colorGray100

// Temporary color, which is not in the token right now
val SemanticColors.temporaryColorBgSpecial
    get() = if (isLight) Color(0xFFE4E8F0) else Color(0xFF3A4048)

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.pinDigitBg
    get() = if (isLight) LightColorTokens.colorGray300 else DarkColorTokens.colorGray400

@OptIn(CoreColorToken::class)
val SemanticColors.pinnedMessageBannerIndicator
    get() = if (isLight) LightColorTokens.colorAlphaGray600 else DarkColorTokens.colorAlphaGray600

@OptIn(CoreColorToken::class)
val SemanticColors.pinnedMessageBannerBorder
    get() = if (isLight) LightColorTokens.colorAlphaGray400 else DarkColorTokens.colorAlphaGray400

@PreviewsDayNight
@Composable
internal fun ColorAliasesPreview() = PRISMPreview {
    ColorListPreview(
        backgroundColor = Color.Black,
        foregroundColor = Color.White,
        colors = persistentMapOf(
            "roomListRoomName" to PRISMTheme.colors.roomListRoomName,
            "roomListRoomMessage" to PRISMTheme.colors.roomListRoomMessage,
            "roomListRoomMessageDate" to PRISMTheme.colors.roomListRoomMessageDate,
            "unreadIndicator" to PRISMTheme.colors.unreadIndicator,
            "placeholderBackground" to PRISMTheme.colors.placeholderBackground,
            "messageFromMeBackground" to PRISMTheme.colors.messageFromMeBackground,
            "messageFromOtherBackground" to PRISMTheme.colors.messageFromOtherBackground,
            "progressIndicatorTrackColor" to PRISMTheme.colors.progressIndicatorTrackColor,
            "temporaryColorBgSpecial" to PRISMTheme.colors.temporaryColorBgSpecial,
        )
    )
}
