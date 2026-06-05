/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.ftue.impl.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.compound.theme.PRISMTheme
import io.prism.android.features.ftue.impl.state.DefaultFtueService
import io.prism.android.libraries.designsystem.theme.components.Button
import io.prism.android.libraries.designsystem.theme.components.Text

class PrismAIPromotionNode(
    buildContext: BuildContext,
    private val ftueService: DefaultFtueService,
    private val onBack: () -> Unit,
) : Node(buildContext) {
    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Welcome to PrismAI",
                style = PRISMTheme.typography.fontHeadingMdBold,
                color = PRISMTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Send a message to Meta from your WhatsApp, then long-press that message and tap 'Mark as AI'. " +
                    "After this, the chat will disappear from the main area and a special 'PrismAI' space will be created.",
                style = PRISMTheme.typography.fontBodyMdRegular,
                color = PRISMTheme.colors.textSecondary,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                text = "Got it",
                onClick = {
                    ftueService.completeCurrentStepAndAdvance()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
