/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.preferences.api.PreferencesEntryPoint
import io.prism.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultPreferencesEntryPoint : PreferencesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: PreferencesEntryPoint.Params,
        callback: PreferencesEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<PreferencesFlowNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback)
        )
    }
}

internal fun PreferencesEntryPoint.InitialTarget.toNavTarget() = when (this) {
    is PreferencesEntryPoint.InitialTarget.Root -> PreferencesFlowNode.NavTarget.Root
    is PreferencesEntryPoint.InitialTarget.NotificationSettings -> PreferencesFlowNode.NavTarget.NotificationSettings
    PreferencesEntryPoint.InitialTarget.NotificationTroubleshoot -> PreferencesFlowNode.NavTarget.TroubleshootNotifications
}
