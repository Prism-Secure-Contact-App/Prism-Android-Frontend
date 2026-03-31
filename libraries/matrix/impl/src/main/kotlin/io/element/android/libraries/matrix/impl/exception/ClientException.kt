/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.exception

import io.prism.android.libraries.prism.api.exception.ClientException
import org.prism.rustcomponents.sdk.ClientException as RustClientException

fun Throwable.mapClientException(): ClientException {
    return when (this) {
        is RustClientException -> {
            when (this) {
                is RustClientException.Generic -> ClientException.Generic(message = msg, details = details, cause = this)
                is RustClientException.PRISMApi -> ClientException.PRISMApi(
                    kind = kind.map(),
                    code = code,
                    message = msg,
                    details = details,
                    cause = this,
                )
            }
        }
        else -> ClientException.Other(message ?: "Unknown error", this)
    }
}
