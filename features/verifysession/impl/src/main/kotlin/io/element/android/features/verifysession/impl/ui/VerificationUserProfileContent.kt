/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.verifysession.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.libraries.designsystem.components.avatar.Avatar
import io.prism.android.libraries.designsystem.components.avatar.AvatarSize
import io.prism.android.libraries.designsystem.components.avatar.AvatarType
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.utils.CommonDrawables
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.model.getAvatarData
import io.prism.android.libraries.prism.ui.model.getBestName

/**
 * Ref: https://www.figma.com/design/lMrKOhS8BEb75GXVq7FnNI/ER-96--User-Verification-by-Emoji?node-id=116-52049
 */
@Composable
fun VerificationUserProfileContent(
    user: PRISMUser,
    modifier: Modifier = Modifier,
) {
    val avatarData = remember(user) {
        user.getAvatarData(AvatarSize.UserVerification)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PRISMTheme.colors.bgSubtleSecondary)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            avatarData = avatarData,
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = user.getBestName(),
                style = PRISMTheme.typography.fontBodyLgMedium,
                color = PRISMTheme.colors.textPrimary,
            )

            if (user.displayName.isNullOrEmpty().not()) {
                Text(
                    text = user.userId.value,
                    style = PRISMTheme.typography.fontBodyMdRegular,
                    color = PRISMTheme.colors.textSecondary,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun VerificationUserProfileContentPreview() = PRISMPreview(
    drawableFallbackForImages = CommonDrawables.sample_avatar
) {
    VerificationUserProfileContent(
        user = PRISMUser(
            userId = UserId("@alice:example.com"),
            displayName = "Alice",
            avatarUrl = "https://example.com/avatar.png",
        )
    )
}
