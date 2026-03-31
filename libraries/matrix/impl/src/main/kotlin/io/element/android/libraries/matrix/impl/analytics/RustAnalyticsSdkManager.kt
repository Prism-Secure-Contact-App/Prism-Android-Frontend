/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.analytics

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.services.analytics.api.AnalyticsSdkManager
import io.prism.android.services.analytics.api.AnalyticsSdkSpan
import org.prism.rustcomponents.sdk.enableSentryLogging

@ContributesBinding(AppScope::class)
class RustAnalyticsSdkManager : AnalyticsSdkManager {
    override fun enableSdkAnalytics(enabled: Boolean) {
        enableSentryLogging(enabled)
    }

    override fun startSpan(name: String, parentTraceId: String?): AnalyticsSdkSpan {
        return RustAnalyticsSdkSpan(name = name, parentTraceId = parentTraceId)
    }

    override fun bridge(parentTraceId: String?): AnalyticsSdkSpan {
        // A bridge span has no name
        return RustAnalyticsSdkSpan(name = null, parentTraceId = parentTraceId)
    }
}
