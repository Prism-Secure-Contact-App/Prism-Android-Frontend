/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.core

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.prism.api.core.SendHandle

class RustSendHandle(
    val inner: org.prism.rustcomponents.sdk.SendHandle,
) : SendHandle {
    override suspend fun retry(): Result<Unit> {
        return runCatchingExceptions {
            inner.tryResend()
        }
    }
}
