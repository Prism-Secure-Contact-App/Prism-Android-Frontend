/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.enterprise.impl

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.enterprise.api.SessionEnterpriseService
import io.prism.android.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultSessionEnterpriseService : SessionEnterpriseService {
    override suspend fun init() = Unit
    override suspend fun isPRISMCallAvailable(): Boolean = true
}
