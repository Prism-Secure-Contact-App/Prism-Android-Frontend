/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.encryption

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.UserId
import io.prism.android.libraries.prism.api.encryption.IdentityOidcResetHandle
import io.prism.android.libraries.prism.api.encryption.IdentityPasswordResetHandle
import io.prism.android.libraries.prism.api.encryption.IdentityResetHandle
import org.prism.rustcomponents.sdk.AuthData
import org.prism.rustcomponents.sdk.AuthDataPasswordDetails
import org.prism.rustcomponents.sdk.CrossSigningResetAuthType

object RustIdentityResetHandleFactory {
    fun create(
        userId: UserId,
        identityResetHandle: org.prism.rustcomponents.sdk.IdentityResetHandle?
    ): Result<IdentityResetHandle?> {
        return runCatchingExceptions {
            identityResetHandle?.let {
                when (val authType = identityResetHandle.authType()) {
                    is CrossSigningResetAuthType.Oidc -> RustOidcIdentityResetHandle(identityResetHandle, authType.info.approvalUrl)
                    // User interactive authentication (user + password)
                    CrossSigningResetAuthType.Uiaa -> RustPasswordIdentityResetHandle(userId, identityResetHandle)
                }
            }
        }
    }
}

class RustPasswordIdentityResetHandle(
    private val userId: UserId,
    private val identityResetHandle: org.prism.rustcomponents.sdk.IdentityResetHandle,
) : IdentityPasswordResetHandle {
    override suspend fun resetPassword(password: String): Result<Unit> {
        return runCatchingExceptions { identityResetHandle.reset(AuthData.Password(AuthDataPasswordDetails(userId.value, password))) }
    }

    override suspend fun cancel() {
        identityResetHandle.cancelAndDestroy()
    }
}

class RustOidcIdentityResetHandle(
    private val identityResetHandle: org.prism.rustcomponents.sdk.IdentityResetHandle,
    override val url: String,
) : IdentityOidcResetHandle {
    override suspend fun resetOidc(): Result<Unit> {
        return runCatchingExceptions { identityResetHandle.reset(null) }
    }

    override suspend fun cancel() {
        identityResetHandle.cancelAndDestroy()
    }
}

private suspend fun org.prism.rustcomponents.sdk.IdentityResetHandle.cancelAndDestroy() {
    cancel()
    destroy()
}
