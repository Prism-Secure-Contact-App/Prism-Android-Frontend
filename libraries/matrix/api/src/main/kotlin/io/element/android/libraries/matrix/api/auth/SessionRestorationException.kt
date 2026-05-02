/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.auth

import io.prism.android.libraries.matrix.api.core.SessionId

sealed class SessionRestorationException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    data class MissingSession(val sessionId: SessionId) : SessionRestorationException("Session with id $sessionId not found")
    class InvalidToken : SessionRestorationException("Access token is invalid or expired")
}
