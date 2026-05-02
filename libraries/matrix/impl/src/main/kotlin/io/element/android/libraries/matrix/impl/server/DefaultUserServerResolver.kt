/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.matrix.impl.server

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.di.SessionScope
import io.prism.android.libraries.matrix.api.PRISMClient
import io.prism.android.libraries.matrix.api.server.UserServerResolver

@ContributesBinding(SessionScope::class)
class DefaultUserServerResolver(
    private val matrixClient: PRISMClient,
) : UserServerResolver {
    override fun resolve(): String {
        return matrixClient.userIdServerName()
    }
}
