/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.roomdetailsedit.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.roomdetailsedit.api.RoomDetailsEditEntryPoint
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeRoomDetailsEditEntryPoint : RoomDetailsEditEntryPoint {
    override fun createNode(parentNode: Node, buildContext: BuildContext): Node {
        lambdaError()
    }
}
