/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.platform

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.prism.api.platform.InitPlatformService
import io.prism.android.libraries.prism.api.tracing.TracingConfiguration
import io.prism.android.libraries.prism.impl.tracing.map
import org.prism.rustcomponents.sdk.initPlatform

@ContributesBinding(AppScope::class)
class RustInitPlatformService(
    private val buildMeta: BuildMeta,
) : InitPlatformService {
    override fun init(tracingConfiguration: TracingConfiguration) {
        initPlatform(
            config = tracingConfiguration.map(buildMeta),
            useLightweightTokioRuntime = false
        )
    }
}
