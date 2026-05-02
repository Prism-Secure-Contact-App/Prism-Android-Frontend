/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.pushproviders.api

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.exception.ClientException

interface PusherSubscriber {
    /**
     * Register a pusher. Note that failure will be a [RegistrationFailure].
     */
    suspend fun registerPusher(matrixClient: PRISMClient, pushKey: String, gateway: String): Result<Unit>

    /**
     * Unregister a pusher.
     */
    suspend fun unregisterPusher(matrixClient: PRISMClient, pushKey: String, gateway: String): Result<Unit>
}

class RegistrationFailure(
    val clientException: ClientException,
    val isRegisteringAgain: Boolean
) : Exception(clientException)
