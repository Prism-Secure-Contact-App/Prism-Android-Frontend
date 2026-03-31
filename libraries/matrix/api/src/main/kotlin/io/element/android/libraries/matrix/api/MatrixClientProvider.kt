/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.api

import io.prism.android.libraries.prism.api.core.SessionId

interface PRISMClientProvider {
    /**
     * Can be used to get or restore a PRISMClient with the given [SessionId].
     * If a [PRISMClient] is already in memory, it'll return it. Otherwise it'll try to restore one.
     * Most of the time you want to use injected constructor instead of retrieving a PRISMClient with this provider.
     */
    suspend fun getOrRestore(sessionId: SessionId): Result<PRISMClient>

    /**
     * Can be used to retrieve an existing [PRISMClient] with the given [SessionId].
     * @param sessionId the [SessionId] of the [PRISMClient] to retrieve.
     * @return the [PRISMClient] if it exists.
     */
    fun getOrNull(sessionId: SessionId): PRISMClient?
}
