/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.prism.android.features.home.impl.R
import io.prism.android.libraries.designsystem.components.Announcement
import io.prism.android.libraries.designsystem.components.AnnouncementType
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight

@Composable
internal fun SetUpRecoveryKeyBanner(
    onContinueClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_set_up_recovery_title),
        description = stringResource(R.string.banner_set_up_recovery_content),
        type = AnnouncementType.Actionable(
            actionText = stringResource(R.string.banner_set_up_recovery_submit),
            onActionClick = onContinueClick,
            onDismissClick = onDismissClick,
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun SetUpRecoveryKeyBannerPreview() = PRISMPreview {
    SetUpRecoveryKeyBanner(
        onContinueClick = {},
        onDismissClick = {},
    )
}
