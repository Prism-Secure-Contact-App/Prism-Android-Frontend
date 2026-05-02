/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.reportroom.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.reportroom.api.ReportRoomEntryPoint
import io.prism.android.libraries.matrix.api.core.RoomId
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeReportRoomEntryPoint : ReportRoomEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        roomId: RoomId,
    ): Node {
        lambdaError()
    }
}
