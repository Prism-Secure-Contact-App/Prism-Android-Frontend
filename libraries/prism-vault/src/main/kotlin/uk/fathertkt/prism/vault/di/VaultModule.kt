/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 */

package uk.fathertkt.prism.vault.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.prism.android.libraries.di.annotations.ApplicationContext
import uk.fathertkt.prism.vault.VaultManager

@BindingContainer
@ContributesTo(AppScope::class)
object VaultModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideVaultManager(
        @ApplicationContext context: Context,
    ): VaultManager = VaultManager(context)
}
