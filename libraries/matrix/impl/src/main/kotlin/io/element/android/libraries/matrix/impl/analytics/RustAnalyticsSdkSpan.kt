/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.analytics

import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.services.analytics.api.AnalyticsSdkSpan
import kotlinx.coroutines.DelicateCoroutinesApi
import org.matrix.rustcomponents.sdk.LogLevel
import org.matrix.rustcomponents.sdk.Span
import timber.log.Timber

class RustAnalyticsSdkSpan(
    name: String? = null,
    private val parentTraceId: String?,
) : AnalyticsSdkSpan {
    private val inner = if (name != null) {
        Span(
            target = "prismx",
            name = name,
            file = "-",
            line = null,
            level = LogLevel.WARN,
            bridgeTraceId = parentTraceId,
        )
    } else {
        Span.newBridgeSpan(
            target = "prismx",
            parentTraceId = parentTraceId,
        )
    }

    override fun enter() {
        if (Span.current().isNone()) {
            inner.enter()
        } else {
            Timber.w("Not entering span sentry.trace='$parentTraceId' because another span is already active")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun exit() {
        inner.exit()
        runCatchingExceptions { inner.destroy() }
        Timber.d("Exited span sentry.trace='$parentTraceId'")
    }
}
