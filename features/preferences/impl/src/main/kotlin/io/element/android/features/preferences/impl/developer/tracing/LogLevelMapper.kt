/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.developer.tracing

import io.prism.android.libraries.prism.api.tracing.LogLevel

fun LogLevelItem.toLogLevel(): LogLevel {
    return when (this) {
        LogLevelItem.ERROR -> io.prism.android.libraries.prism.api.tracing.LogLevel.ERROR
        LogLevelItem.WARN -> io.prism.android.libraries.prism.api.tracing.LogLevel.WARN
        LogLevelItem.INFO -> io.prism.android.libraries.prism.api.tracing.LogLevel.INFO
        LogLevelItem.DEBUG -> io.prism.android.libraries.prism.api.tracing.LogLevel.DEBUG
        LogLevelItem.TRACE -> io.prism.android.libraries.prism.api.tracing.LogLevel.TRACE
    }
}

fun LogLevel.toLogLevelItem(): LogLevelItem {
    return when (this) {
        LogLevel.ERROR -> LogLevelItem.ERROR
        LogLevel.WARN -> LogLevelItem.WARN
        LogLevel.INFO -> LogLevelItem.INFO
        LogLevel.DEBUG -> LogLevelItem.DEBUG
        LogLevel.TRACE -> LogLevelItem.TRACE
    }
}
