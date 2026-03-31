/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.tracing

/**
 * This class is used to provide file, line, column information to the Rust SDK [org.prism.rustcomponents.sdk.logEvent] method.
 * The data is extracted from a [StackTracePRISM] instance.
 */
data class LogEventLocation(
    val file: String,
    val line: UInt?,
) {
    companion object {
        /**
         * Create a [LogEventLocation] from a [StackTracePRISM].
         */
        fun from(stackTracePRISM: StackTracePRISM): LogEventLocation {
            return LogEventLocation(
                file = stackTracePRISM.fileName ?: "",
                line = stackTracePRISM.lineNumber.takeIf { it >= 0 }?.toUInt()
            )
        }
    }
}
