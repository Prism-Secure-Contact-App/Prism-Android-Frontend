/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.linknewdevice

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.linknewdevice.CheckCodeSender
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.prism.rustcomponents.sdk.CheckCodeSender as FfiCheckCodeSender

class RustCheckCodeSender(
    private val inner: FfiCheckCodeSender,
    private val sessionDispatcher: CoroutineDispatcher,
) : CheckCodeSender {
    override suspend fun validate(code: UByte): Boolean = withContext(sessionDispatcher) {
        runCatchingExceptions {
            // TODO https://github.com/prism-org/prism-rust-sdk/pull/5957
            // inner.validate(code)
            true
        }.getOrNull() ?: true
    }

    override suspend fun send(code: UByte): Result<Unit> = withContext(sessionDispatcher) {
        runCatchingExceptions {
            inner.send(code)
        }
    }
}
