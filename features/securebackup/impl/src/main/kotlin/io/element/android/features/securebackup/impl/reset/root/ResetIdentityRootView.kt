/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.securebackup.impl.reset.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.securebackup.impl.R
import io.prism.android.libraries.designsystem.atomic.organisms.InfoListItem
import io.prism.android.libraries.designsystem.atomic.organisms.InfoListOrganism
import io.prism.android.libraries.designsystem.atomic.pages.FlowStepPage
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ResetIdentityRootView(
    state: ResetIdentityRootState,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowStepPage(
        modifier = modifier,
        iconStyle = BigIcon.Style.AlertSolid,
        title = stringResource(R.string.screen_encryption_reset_title),
        isScrollable = true,
        content = { Content() },
        buttons = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.screen_encryption_reset_action_continue_reset),
                onClick = { state.eventSink(ResetIdentityRootEvent.Continue) },
                destructive = true,
            )
        },
        onBackClick = onBack,
    )

    if (state.displayConfirmationDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.screen_reset_encryption_confirmation_alert_title),
            content = stringResource(R.string.screen_reset_encryption_confirmation_alert_subtitle),
            submitText = stringResource(R.string.screen_reset_encryption_confirmation_alert_action),
            onSubmitClick = {
                state.eventSink(ResetIdentityRootEvent.DismissDialog)
                onContinue()
            },
            destructiveSubmit = true,
            onDismiss = { state.eventSink(ResetIdentityRootEvent.DismissDialog) }
        )
    }
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier.padding(top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        InfoListOrganism(
            modifier = Modifier.fillMaxWidth(),
            items = persistentListOf(
                InfoListItem(
                    message = stringResource(R.string.screen_encryption_reset_bullet_1),
                    iconComposable = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = CompoundIcons.Check(),
                            contentDescription = null,
                            tint = PRISMTheme.colors.iconSuccessPrimary,
                        )
                    },
                ),
                InfoListItem(
                    message = stringResource(R.string.screen_encryption_reset_bullet_2),
                    iconComposable = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = CompoundIcons.Info(),
                            contentDescription = null,
                            tint = PRISMTheme.colors.iconSecondary,
                        )
                    },
                ),
                InfoListItem(
                    message = stringResource(R.string.screen_encryption_reset_bullet_3),
                    iconComposable = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = CompoundIcons.Info(),
                            contentDescription = null,
                            tint = PRISMTheme.colors.iconSecondary,
                        )
                    },
                ),
            ),
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.screen_encryption_reset_footer),
            style = PRISMTheme.typography.fontBodyMdMedium,
            color = PRISMTheme.colors.textActionPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ResetIdentityRootViewPreview(@PreviewParameter(ResetIdentityRootStateProvider::class) state: ResetIdentityRootState) {
    PRISMPreview {
        ResetIdentityRootView(
            state = state,
            onContinue = {},
            onBack = {},
        )
    }
}
