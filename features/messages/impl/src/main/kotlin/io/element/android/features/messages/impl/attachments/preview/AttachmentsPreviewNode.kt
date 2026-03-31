/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.messages.impl.attachments.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.prism.android.annotations.ContributesNode
import io.prism.android.compound.colors.SemanticColorsLightDark
import io.prism.android.compound.theme.ForcedDarkPRISMTheme
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.messages.impl.attachments.Attachment
import io.prism.android.libraries.architecture.NodeInputs
import io.prism.android.libraries.architecture.inputs
import io.prism.android.libraries.di.RoomScope
import io.prism.android.libraries.prism.api.core.EventId
import io.prism.android.libraries.prism.api.core.SessionId
import io.prism.android.libraries.prism.api.timeline.Timeline
import io.prism.android.libraries.mediaviewer.api.local.LocalMediaRenderer

@ContributesNode(RoomScope::class)
@AssistedInject
class AttachmentsPreviewNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: AttachmentsPreviewPresenter.Factory,
    private val localMediaRenderer: LocalMediaRenderer,
    private val sessionId: SessionId,
    private val enterpriseService: EnterpriseService,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val attachment: Attachment,
        val timelineMode: Timeline.Mode,
        val inReplyToEventId: EventId?,
    ) : NodeInputs

    private val inputs: Inputs = inputs()

    private val onDoneListener = OnDoneListener {
        navigateUp()
    }

    private val presenter = presenterFactory.create(
        attachment = inputs.attachment,
        timelineMode = inputs.timelineMode,
        onDoneListener = onDoneListener,
        inReplyToEventId = inputs.inReplyToEventId,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val colors by remember {
            enterpriseService.semanticColorsFlow(sessionId = sessionId)
        }.collectAsState(SemanticColorsLightDark.default)
        ForcedDarkPRISMTheme(
            colors = colors,
        ) {
            val state = presenter.present()
            AttachmentsPreviewView(
                state = state,
                localMediaRenderer = localMediaRenderer,
                modifier = modifier
            )
        }
    }
}
