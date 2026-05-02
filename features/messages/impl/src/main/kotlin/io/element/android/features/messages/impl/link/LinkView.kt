/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.core.extensions.ensureEndsLeftToRight
import io.prism.android.libraries.core.extensions.filterDirectionOverrides
import io.prism.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.ui.strings.CommonStrings
import io.element.android.wysiwyg.link.Link

@Composable
fun LinkView(
    state: LinkState,
    onLinkValid: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.linkClick) {
        AsyncAction.Uninitialized,
        AsyncAction.Loading,
        is AsyncAction.Failure -> Unit
        is AsyncAction.Confirming -> {
            if (state.linkClick is ConfirmingLinkClick) {
                ConfirmationDialog(
                    modifier = modifier,
                    title = stringResource(CommonStrings.dialog_confirm_link_title),
                    content = stringResource(
                        CommonStrings.dialog_confirm_link_message,
                        state.linkClick.link.text.ensureEndsLeftToRight(),
                        state.linkClick.link.url.filterDirectionOverrides(),
                    ),
                    submitText = stringResource(CommonStrings.action_continue),
                    onSubmitClick = {
                        state.eventSink(LinkEvent.Confirm)
                    },
                    onDismiss = {
                        state.eventSink(LinkEvent.Cancel)
                    },
                )
            }
        }
        is AsyncAction.Success -> {
            val latestOnLinkValid by rememberUpdatedState(onLinkValid)
            LaunchedEffect(state.linkClick.data) {
                latestOnLinkValid(state.linkClick.data)
                state.eventSink(LinkEvent.Cancel)
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LinkViewPreview(@PreviewParameter(LinkStateProvider::class) state: LinkState) = PRISMPreview {
    LinkView(
        state = state,
        onLinkValid = {},
    )
}
