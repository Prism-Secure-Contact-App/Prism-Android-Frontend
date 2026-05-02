/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.api.auth

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.PRISMClientProvider
import io.prism.android.libraries.matrix.api.auth.external.ExternalSession
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.prism.android.libraries.matrix.api.core.SessionId

interface PRISMAuthenticationService {
    /**
     * Restore a session from a [sessionId].
     * Do not restore anything it the access token is not valid anymore.
     * Generally this method should not be used directly, prefer using [PRISMClientProvider.getOrRestore] instead.
     */
    suspend fun restoreSession(sessionId: SessionId): Result<PRISMClient>

    /**
     * Set the homeserver to use for authentication, and return its details.
     */
    suspend fun setHomeserver(homeserver: String): Result<PRISMHomeServerDetails>

    suspend fun login(username: String, password: String): Result<SessionId>

    /**
     * Import a session that was created using another client, for instance PRISM Web.
     */
    suspend fun importCreatedSession(externalSession: ExternalSession): Result<SessionId>

    /*
     * OIDC part.
     */

    /**
     * Get the Oidc url to display to the user.
     */
    suspend fun getOidcUrl(
        prompt: OidcPrompt,
        loginHint: String?,
    ): Result<OidcDetails>

    /**
     * Cancel Oidc login sequence.
     */
    suspend fun cancelOidcLogin(): Result<Unit>

    /**
     * Attempt to login using the [callbackUrl] provided by the Oidc page.
     */
    suspend fun loginWithOidc(callbackUrl: String): Result<SessionId>

    suspend fun loginWithQrCode(qrCodeData: PRISMQrCodeLoginData, progress: (QrCodeLoginStep) -> Unit): Result<SessionId>

    /** Listen to new PRISM clients being created on authentication. */
    fun listenToNewPRISMClients(lambda: (PRISMClient) -> Unit)
}
