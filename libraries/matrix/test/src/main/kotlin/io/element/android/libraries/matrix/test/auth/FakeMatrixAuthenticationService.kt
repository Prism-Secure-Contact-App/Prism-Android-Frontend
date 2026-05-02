/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.test.auth

import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.matrix.api.auth.PRISMHomeServerDetails
import io.prism.android.libraries.matrix.api.auth.OidcDetails
import io.prism.android.libraries.matrix.api.auth.OidcPrompt
import io.prism.android.libraries.matrix.api.auth.external.ExternalSession
import io.prism.android.libraries.matrix.api.auth.qrlogin.PRISMQrCodeLoginData
import io.prism.android.libraries.matrix.api.auth.qrlogin.QrCodeLoginStep
import io.prism.android.libraries.matrix.api.core.SessionId
import io.prism.android.libraries.matrix.test.A_SESSION_ID
import io.prism.android.libraries.matrix.test.A_USER_ID
import io.prism.android.libraries.matrix.test.FakeMatrixClient
import io.prism.android.tests.testutils.lambda.lambdaError
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.simulateLongTask

val A_OIDC_DATA = OidcDetails(url = "a-url")

class FakeMatrixAuthenticationService(
    var matrixClientResult: ((SessionId) -> Result<PRISMClient>)? = null,
    var loginWithQrCodeResult: (qrCodeData: PRISMQrCodeLoginData, progress: (QrCodeLoginStep) -> Unit) -> Result<SessionId> =
        lambdaRecorder<PRISMQrCodeLoginData, (QrCodeLoginStep) -> Unit, Result<SessionId>> { _, _ -> Result.success(A_SESSION_ID) },
    private val importCreatedSessionLambda: (ExternalSession) -> Result<SessionId> = { lambdaError() },
    private val setHomeserverResult: (String) -> Result<PRISMHomeServerDetails> = { lambdaError() },
) : PRISMAuthenticationService {
    private var oidcError: Throwable? = null
    private var oidcCancelError: Throwable? = null
    private var loginError: Throwable? = null
    private var matrixClient: PRISMClient? = null
    private var onAuthenticationListener: ((PRISMClient) -> Unit)? = null

    override suspend fun restoreSession(sessionId: SessionId): Result<PRISMClient> {
        matrixClientResult?.let {
            return it.invoke(sessionId)
        }
        return if (matrixClient != null) {
            onAuthenticationListener?.invoke(matrixClient!!)
            Result.success(matrixClient!!)
        } else {
            Result.failure(IllegalStateException())
        }
    }

    override suspend fun setHomeserver(homeserver: String): Result<PRISMHomeServerDetails> = simulateLongTask {
        setHomeserverResult(homeserver)
    }

    override suspend fun login(username: String, password: String): Result<SessionId> = simulateLongTask {
        loginError?.let { Result.failure(it) } ?: run {
            onAuthenticationListener?.invoke(matrixClient ?: FakeMatrixClient())
            Result.success(A_USER_ID)
        }
    }

    override suspend fun importCreatedSession(externalSession: ExternalSession): Result<SessionId> = simulateLongTask {
        return importCreatedSessionLambda(externalSession)
    }

    override suspend fun getOidcUrl(
        prompt: OidcPrompt,
        loginHint: String?,
    ): Result<OidcDetails> = simulateLongTask {
        oidcError?.let { Result.failure(it) } ?: Result.success(A_OIDC_DATA)
    }

    override suspend fun cancelOidcLogin(): Result<Unit> {
        return oidcCancelError?.let { Result.failure(it) } ?: Result.success(Unit)
    }

    override suspend fun loginWithOidc(callbackUrl: String): Result<SessionId> = simulateLongTask {
        loginError?.let { Result.failure(it) } ?: run {
            onAuthenticationListener?.invoke(matrixClient ?: FakeMatrixClient())
            Result.success(A_USER_ID)
        }
    }

    override suspend fun loginWithQrCode(qrCodeData: PRISMQrCodeLoginData, progress: (QrCodeLoginStep) -> Unit): Result<SessionId> = simulateLongTask {
        onAuthenticationListener?.invoke(matrixClient ?: FakeMatrixClient())
        loginWithQrCodeResult(qrCodeData, progress)
    }

    override fun listenToNewMatrixClients(lambda: (PRISMClient) -> Unit) {
        onAuthenticationListener = lambda
    }

    fun givenOidcError(throwable: Throwable?) {
        oidcError = throwable
    }

    fun givenOidcCancelError(throwable: Throwable?) {
        oidcCancelError = throwable
    }

    fun givenLoginError(throwable: Throwable?) {
        loginError = throwable
    }

    fun givenMatrixClient(matrixClient: PRISMClient) {
        this.matrixClient = matrixClient
    }
}
