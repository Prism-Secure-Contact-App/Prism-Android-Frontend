/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.createroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.prism.android.libraries.prism.api.core.RoomId
import io.prism.android.tests.testutils.lambda.lambdaError

class FakeCreateRoomEntryPoint : CreateRoomEntryPoint {
    class Builder : CreateRoomEntryPoint.Builder {
        override fun setIsSpace(isSpace: Boolean): Builder = this
        override fun setParentSpace(parentSpaceId: RoomId): Builder = this
        override fun build(): Node = lambdaError()
    }

    override fun builder(
        parentNode: Node,
        buildContext: BuildContext,
        callback: CreateRoomEntryPoint.Callback,
    ): Builder = lambdaError()
}
