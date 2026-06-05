/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.prism.android.compound.tokens.generated.CompoundIcons
import io.prism.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.prism.android.libraries.designsystem.components.BigIcon
import io.prism.android.libraries.designsystem.components.button.BackButton
import io.prism.android.libraries.designsystem.theme.components.Scaffold
import io.prism.android.libraries.designsystem.theme.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WhatsAppBridgeView(
    state: BridgeState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onClick = { state.eventSink(BridgeEvents.Back) })
                },
            )
        },
    ) { padding ->
        // imePadding() lifts the whole column above the on-screen keyboard so the
        // phone-number TextField stays visible while typing. We make the column
        // scrollable so smaller phones can still reach the bottom controls when
        // the keyboard is open.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconTitleSubtitleMolecule(
                iconStyle = BigIcon.Style.Default(CompoundIcons.ShareAndroid()),
                title = "Connect WhatsApp",
                subTitle = "View and reply to WhatsApp chats from within PRISM. " +
                    "Messages are grouped in a separate 'WhatsApp' space; your main screen stays clean.",
            )

            Spacer(Modifier.height(24.dp))

            BridgePhaseContent(
                phase = state.phase,
                bridgeName = "WhatsApp",
                eventSink = state.eventSink,
            )

            Spacer(Modifier.height(24.dp))

            BridgeActionRow(
                state = state,
                primaryLabel = if (state.phase is UiPhase.Error) "Try again" else "WhatsApp'ı bağla",
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
