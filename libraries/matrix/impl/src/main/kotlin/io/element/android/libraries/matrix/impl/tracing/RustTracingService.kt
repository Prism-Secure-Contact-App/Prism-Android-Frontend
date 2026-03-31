/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.tracing

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.data.ByteUnit
import io.prism.android.libraries.core.data.megaBytes
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.prism.api.tracing.LogLevel
import io.prism.android.libraries.prism.api.tracing.TracingConfiguration
import io.prism.android.libraries.prism.api.tracing.TracingService
import io.prism.android.libraries.prism.api.tracing.WriteToFilesConfiguration
import org.prism.rustcomponents.sdk.SentryConfig
import org.prism.rustcomponents.sdk.TracingFileConfiguration
import org.prism.rustcomponents.sdk.reloadTracingFileWriter
import timber.log.Timber

@ContributesBinding(AppScope::class)
class RustTracingService(private val buildMeta: BuildMeta) : TracingService {
    override fun createTimberTree(target: String): Timber.Tree {
        return RustTracingTree(target = target, retrieveFromStackTrace = buildMeta.isDebuggable)
    }

    override fun updateWriteToFilesConfiguration(config: WriteToFilesConfiguration) {
        config.toTracingFileConfiguration()?.let {
            reloadTracingFileWriter(it)
        }
    }
}

private fun LogLevel.toRustLogLevel(): org.prism.rustcomponents.sdk.LogLevel {
    return when (this) {
        LogLevel.ERROR -> org.prism.rustcomponents.sdk.LogLevel.ERROR
        LogLevel.WARN -> org.prism.rustcomponents.sdk.LogLevel.WARN
        LogLevel.INFO -> org.prism.rustcomponents.sdk.LogLevel.INFO
        LogLevel.DEBUG -> org.prism.rustcomponents.sdk.LogLevel.DEBUG
        LogLevel.TRACE -> org.prism.rustcomponents.sdk.LogLevel.TRACE
    }
}

private fun WriteToFilesConfiguration.toTracingFileConfiguration(): TracingFileConfiguration? {
    return when (this) {
        is WriteToFilesConfiguration.Disabled -> null
        is WriteToFilesConfiguration.Enabled -> TracingFileConfiguration(
            path = directory,
            filePrefix = filenamePrefix,
            fileSuffix = filenameSuffix,
            // Have at max 100MB of logs in disk
            maxTotalSizeBytes = 100.megaBytes.into(ByteUnit.BYTES).toULong(),
            // Store up to 7 days of logs
            maxAgeSeconds = (7 * 24 * 60 * 60).toULong(),
        )
    }
}

fun TracingConfiguration.map(buildMeta: BuildMeta): org.prism.rustcomponents.sdk.TracingConfiguration = org.prism.rustcomponents.sdk.TracingConfiguration(
    writeToStdoutOrSystem = writesToLogcat,
    logLevel = logLevel.toRustLogLevel(),
    extraTargets = extraTargets,
    traceLogPacks = traceLogPacks.map(),
    writeToFiles = writesToFilesConfiguration.toTracingFileConfiguration(),
    sentryConfig = sdkSentryDsn?.let {
        SentryConfig(
            dsn = it,
            appVersion = buildMeta.versionName,
            appPlatform = "Android",
        )
    }
)
