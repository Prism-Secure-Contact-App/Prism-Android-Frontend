/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.linknewdevice.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.prism.android.libraries.architecture.FeatureEntryPoint

interface LinkNewDeviceEntryPoint : FeatureEntryPoint {
    interface Callback : Plugin {
        fun onDone()
    }

    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node
}
