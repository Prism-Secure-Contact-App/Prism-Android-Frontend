/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rageshake.impl.bugreport

import io.prism.android.features.rageshake.api.reporter.BugReporter
import io.prism.android.features.rageshake.api.reporter.BugReporterListener
import io.prism.android.libraries.prism.test.A_FAILURE_REASON
import kotlinx.coroutines.delay
import java.io.File

class FakeBugReporter(val mode: Mode = Mode.Success) : BugReporter {
    enum class Mode {
        Success,
        Failure,
        Cancel
    }

    override suspend fun sendBugReport(
        withDevicesLogs: Boolean,
        withCrashLogs: Boolean,
        withScreenshot: Boolean,
        problemDescription: String,
        canContact: Boolean,
        sendPushRules: Boolean,
        listener: BugReporterListener,
    ) {
        delay(100)
        listener.onProgress(0)
        delay(100)
        listener.onProgress(50)
        delay(100)
        when (mode) {
            Mode.Success -> Unit
            Mode.Failure -> {
                listener.onUploadFailed(A_FAILURE_REASON)
                return
            }
            Mode.Cancel -> {
                listener.onUploadCancelled()
                return
            }
        }
        listener.onProgress(100)
        delay(100)
        listener.onUploadSucceed()
    }

    override fun logDirectory(): File {
        return File("fake")
    }

    override fun setCurrentTracingLogLevel(logLevel: String) {
        // No op
    }

    override fun saveLogCat(): File? {
        return null
    }
}
