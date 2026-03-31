/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.analytics.impl

import io.prism.android.features.analytics.api.AnalyticsOptInEvents

data class AnalyticsOptInState(
    val applicationName: String,
    val hasPolicyLink: Boolean,
    val eventSink: (AnalyticsOptInEvents) -> Unit
)
