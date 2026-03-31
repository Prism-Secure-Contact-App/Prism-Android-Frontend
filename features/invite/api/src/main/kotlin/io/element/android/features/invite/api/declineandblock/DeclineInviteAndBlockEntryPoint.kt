/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.invite.api.declineandblock

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.invite.api.InviteData
import io.prism.android.libraries.architecture.FeatureEntryPoint

fun interface DeclineInviteAndBlockEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inviteData: InviteData,
    ): Node
}
