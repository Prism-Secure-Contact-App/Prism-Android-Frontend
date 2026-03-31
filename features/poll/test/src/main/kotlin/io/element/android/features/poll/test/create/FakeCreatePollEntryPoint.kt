/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.poll.test.create

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.features.poll.api.create.CreatePollEntryPoint
import io.prism.android.features.poll.api.create.CreatePollEntryPoint.Params
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeCreatePollEntryPoint : CreatePollEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
    ): Node = lambdaError()
}
