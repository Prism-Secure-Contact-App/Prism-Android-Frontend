/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.location.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.libraries.architecture.FeatureEntryPoint
import io.prism.android.libraries.prism.api.timeline.Timeline

/**
 * The "Share location" screen.
 *
 * Allows a user to share a location message within a room.
 */
interface ShareLocationEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        timelineMode: Timeline.Mode,
    ): Node
}
