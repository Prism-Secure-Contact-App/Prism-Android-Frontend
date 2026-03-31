/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.impl.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.features.linknewdevice.impl.R
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.designsystem.atomic.pages.FlowStepPage
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.modifiers.cornerBorder
import io.prism.android.libraries.designsystem.modifiers.squareSize
import io.prism.android.libraries.designsystem.preview.PRISMPreview
import io.prism.android.libraries.designsystem.preview.PreviewsDayNight
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.prism.android.libraries.designsystem.theme.components.Icon
import io.prism.android.libraries.designsystem.theme.components.Text
import io.prism.android.libraries.qrcode.QrCodeCameraView
import io.prism.android.libraries.ui.strings.CommonStrings

@Composable
fun ScanQrCodeView(
    state: ScanQrCodeState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowStepPage(
        modifier = modifier,
        onBackClick = onBackClick,
        iconStyle = BigIcon.Style.Default(CompoundIcons.Computer()),
        title = stringResource(R.string.screen_link_new_device_desktop_scanning_title),
        content = { Content(state = state) },
        buttons = { Buttons(state = state) }
    )
}

@Composable
private fun Content(
    state: ScanQrCodeState,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val modifier = if (constraints.maxWidth > constraints.maxHeight) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.fillMaxWidth()
        }.then(
            Modifier
                .padding(start = 20.dp, end = 20.dp, top = 50.dp, bottom = 32.dp)
                .squareSize()
                .cornerBorder(
                    strokeWidth = 4.dp,
                    color = PRISMTheme.colors.textPrimary,
                    cornerSizeDp = 42.dp,
                )
        )
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            QrCodeCameraView(
                modifier = Modifier.fillMaxSize(),
                onScanQrCode = { state.eventSink.invoke(ScanQrCodeEvent.QrCodeScanned(it)) },
                isScanning = state.scanAction.isLoading(),
            )
        }
    }
}

@Composable
private fun ColumnScope.Buttons(
    state: ScanQrCodeState,
) {
    Column(Modifier.heightIn(min = 130.dp)) {
        when (state.scanAction) {
            is AsyncAction.Failure -> {
                Button(
                    text = stringResource(id = CommonStrings.action_try_again),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onClick = {
                        state.eventSink.invoke(ScanQrCodeEvent.TryAgain)
                    }
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = CompoundIcons.ErrorSolid(),
                            tint = PRISMTheme.colors.iconCriticalPrimary,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.screen_qr_code_login_invalid_scan_state_subtitle),
                            textAlign = TextAlign.Center,
                            color = PRISMTheme.colors.textCriticalPrimary,
                            style = PRISMTheme.typography.fontBodySmMedium,
                        )
                    }
                    Text(
                        text = stringResource(R.string.screen_qr_code_login_invalid_scan_state_description),
                        textAlign = TextAlign.Center,
                        style = PRISMTheme.typography.fontBodySmRegular,
                        color = PRISMTheme.colors.textSecondary,
                    )
                }
            }
            is AsyncAction.Success -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .progressSemantics()
                            .size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            AsyncAction.Loading,
            AsyncAction.Uninitialized,
            is AsyncAction.Confirming -> Unit
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ScanQrCodeViewPreview(@PreviewParameter(ScanQrCodeStateProvider::class) state: ScanQrCodeState) = PRISMPreview {
    ScanQrCodeView(
        state = state,
        onBackClick = {},
    )
}
