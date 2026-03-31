/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.signedout.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.prism.api.core.SessionId

interface SignedOutEntryPoint : FeatureEntryPoint {
    data class Params(
        val sessionId: SessionId,
    )

    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
    ): Node
}
