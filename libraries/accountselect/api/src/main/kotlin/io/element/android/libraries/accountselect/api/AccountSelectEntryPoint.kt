/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.accountselect.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.prism.api.core.SessionId

interface AccountSelectEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    interface Callback : Plugin {
        fun onAccountSelected(sessionId: SessionId)
        fun onCancel()
    }
}
