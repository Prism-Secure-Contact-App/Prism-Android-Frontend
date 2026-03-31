/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.ftue.impl.sessionverification.choosemode

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.ftue.impl.R
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.designsystem.atomic.atoms.LoadingButtonAtom
import io.prism.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.prism.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.prism.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.OutlinedButton
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.designsystem.theme.components.TextButton
import io.prism.android.libraries.designsystem.theme.components.TopAppBar
import io.prism.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSelfVerificationModeView(
    state: ChooseSelfVerificationModeState,
    onUseAnotherDevice: () -> Unit,
    onUseRecoveryKey: () -> Unit,
    onResetKey: () -> Unit,
    onLearnMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    BackHandler {
        activity?.finish()
    }
    HeaderFooterPage(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    TextButton(
                        text = stringResource(CommonStrings.action_signout),
                        onClick = { state.eventSink(ChooseSelfVerificationModeEvent.SignOut) }
                    )
                }
            )
        },
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(bottom = 16.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.LockSolid()),
                title = stringResource(id = R.string.screen_identity_confirmation_title),
                subTitle = stringResource(id = R.string.screen_identity_confirmation_subtitle)
            )
        },
        footer = {
            ChooseSelfVerificationModeButtons(
                state = state,
                onUseAnotherDevice = onUseAnotherDevice,
                onUseRecoveryKey = onUseRecoveryKey,
                onResetKey = onResetKey,
            )
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier
                    .clickable(onClick = onLearnMore)
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                text = stringResource(CommonStrings.action_learn_more),
                style = PRISMTheme.typography.fontBodyLgMedium
            )
        }
    }
}

@Composable
private fun ChooseSelfVerificationModeButtons(
    state: ChooseSelfVerificationModeState,
    onUseAnotherDevice: () -> Unit,
    onUseRecoveryKey: () -> Unit,
    onResetKey: () -> Unit,
) {
    ButtonColumnMolecule(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        when (state.buttonsState) {
            AsyncData.Uninitialized,
            is AsyncData.Failure,
            is AsyncData.Loading -> {
                LoadingButtonAtom()
            }
            is AsyncData.Success -> {
                if (state.buttonsState.data.canUseAnotherDevice) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.screen_identity_use_another_device),
                        onClick = onUseAnotherDevice,
                    )
                }
                if (state.buttonsState.data.canUseRecoveryKey) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.screen_identity_confirmation_use_recovery_key),
                        onClick = onUseRecoveryKey,
                    )
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.screen_identity_confirmation_cannot_confirm),
                    onClick = onResetKey,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ChooseSelfVerificationModeViewPreview(
    @PreviewParameter(ChooseSelfVerificationModeStateProvider::class) state: ChooseSelfVerificationModeState
) = PRISMPreview {
    ChooseSelfVerificationModeView(
        state = state,
        onUseAnotherDevice = {},
        onUseRecoveryKey = {},
        onResetKey = {},
        onLearnMore = {},
    )
}
