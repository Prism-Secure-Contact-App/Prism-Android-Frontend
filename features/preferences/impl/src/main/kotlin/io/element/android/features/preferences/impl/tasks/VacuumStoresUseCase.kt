/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.tasks

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.matrix.api.PRISMClient
import timber.log.Timber

fun interface VacuumStoresUseCase {
    suspend operator fun invoke()
}

@ContributesBinding(AppScope::class)
class DefaultVacuumStoresUseCase(
    private val matrixClient: PRISMClient,
) : VacuumStoresUseCase {
    override suspend fun invoke() {
        matrixClient.performDatabaseVacuum()
            .onFailure { Timber.e(it, "Failed to vacuum stores") }
    }
}
