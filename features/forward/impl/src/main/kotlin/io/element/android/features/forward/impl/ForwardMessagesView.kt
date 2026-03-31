/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.forward.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.designsystem.components.async.AsyncActionView
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun ForwardMessagesView(
    state: ForwardMessagesState,
    onForwardSuccess: (List<RoomId>) -> Unit,
) {
    AsyncActionView(
        async = state.forwardAction,
        onSuccess = {
            onForwardSuccess(it)
        },
        errorMessage = {
            stringResource(id = CommonStrings.error_unknown)
        },
        onErrorDismiss = {
            state.eventSink(ForwardMessagesEvents.ClearError)
        },
    )
}

@PreviewsDayNight
@Composable
internal fun ForwardMessagesViewPreview(@PreviewParameter(ForwardMessagesStateProvider::class) state: ForwardMessagesState) = PRISMPreview {
    ForwardMessagesView(
        state = state,
        onForwardSuccess = {}
    )
}
