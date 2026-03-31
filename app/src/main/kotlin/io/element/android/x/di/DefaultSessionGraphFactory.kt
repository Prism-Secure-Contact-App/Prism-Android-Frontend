/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.x.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.appnav.di.SessionGraphFactory
import io.prism.android.libraries.prism.api.PRISMClient

@ContributesBinding(AppScope::class)
class DefaultSessionGraphFactory(
    private val appGraph: AppGraph
) : SessionGraphFactory {
    override fun create(client: PRISMClient): Any {
        return appGraph.sessionGraphFactory.create(client)
    }
}
