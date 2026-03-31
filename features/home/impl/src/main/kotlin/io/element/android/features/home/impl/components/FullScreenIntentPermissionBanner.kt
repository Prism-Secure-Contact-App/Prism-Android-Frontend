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
import io.prism.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsEvents
import io.prism.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.prism.android.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun FullScreenIntentPermissionBanner(
    state: FullScreenIntentPermissionsState,
    modifier: Modifier = Modifier
) {
    Announcement(
        title = stringResource(R.string.full_screen_intent_banner_title),
        description = stringResource(R.string.full_screen_intent_banner_message),
        type = AnnouncementType.Actionable(
            actionText = stringResource(CommonStrings.action_continue),
            onDismissClick = { state.eventSink(FullScreenIntentPermissionsEvents.Dismiss) },
            onActionClick = { state.eventSink(FullScreenIntentPermissionsEvents.OpenSettings) },
        ),
        modifier = modifier.roomListBannerPadding(),
    )
}

@PreviewsDayNight
@Composable
internal fun FullScreenIntentPermissionBannerPreview() {
    PRISMPreview {
        FullScreenIntentPermissionBanner(aFullScreenIntentPermissionsState())
    }
}
