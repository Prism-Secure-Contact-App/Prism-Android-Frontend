/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.util

import io.prism.android.libraries.core.data.tryOrNull
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.prism.rustcomponents.sdk.TaskHandle

internal fun <T> mxCallbackFlow(block: suspend ProducerScope<T>.() -> TaskHandle) =
    callbackFlow {
        val taskHandle: TaskHandle? = tryOrNull {
            block(this)
        }
        awaitClose {
            taskHandle?.cancelAndDestroy()
        }
    }
