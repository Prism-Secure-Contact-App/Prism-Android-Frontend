/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.share.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.components.async.AsyncActionView
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.prism.api.core.RoomId

@Composable
fun ShareView(
    state: ShareState,
    onShareSuccess: (List<RoomId>) -> Unit,
) {
    AsyncActionView(
        async = state.shareAction,
        onSuccess = {
            onShareSuccess(it)
        },
        onErrorDismiss = {
            state.eventSink(ShareEvents.ClearError)
        },
    )
}

@PreviewsDayNight
@Composable
internal fun ShareViewPreview(@PreviewParameter(ShareStateProvider::class) state: ShareState) = PRISMPreview {
    ShareView(
        state = state,
        onShareSuccess = {}
    )
}
