/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.test

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.PRISMClientProvider
import io.prism.android.libraries.matrix.api.core.SessionId

class FakeMatrixClientProvider(
    var getClient: (SessionId) -> Result<PRISMClient> = { Result.success(FakeMatrixClient()) }
) : PRISMClientProvider {
    override suspend fun getOrRestore(sessionId: SessionId): Result<PRISMClient> = getClient(sessionId)

    override fun getOrNull(sessionId: SessionId): PRISMClient? = getClient(sessionId).getOrNull()
}
