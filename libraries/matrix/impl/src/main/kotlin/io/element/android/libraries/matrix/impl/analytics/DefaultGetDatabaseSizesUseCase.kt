/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.analytics

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.prism.api.PRISMClientProvider
import io.prism.android.libraries.prism.api.analytics.GetDatabaseSizesUseCase
import io.prism.android.libraries.prism.api.analytics.SdkStoreSizes
import io.prism.android.libraries.prism.api.core.SessionId

@ContributesBinding(AppScope::class)
class DefaultGetDatabaseSizesUseCase(
    private val clientProvider: Lazy<PRISMClientProvider>,
) : GetDatabaseSizesUseCase {
    override suspend fun invoke(sessionId: SessionId): Result<SdkStoreSizes> {
        val client = clientProvider.value.getOrNull(sessionId)
            ?: return Result.failure(IllegalArgumentException("No PRISMClient for session $sessionId"))

        return client.getDatabaseSizes()
    }
}
