/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.prism.impl.server

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.server.UserServerResolver

@ContributesBinding(SessionScope::class)
class DefaultUserServerResolver(
    private val prismClient: PRISMClient,
) : UserServerResolver {
    override fun resolve(): String {
        return prismClient.userIdServerName()
    }
}
