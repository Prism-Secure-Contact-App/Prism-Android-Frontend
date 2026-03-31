/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.ftue.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.prism.android.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModePresenter
import io.prism.android.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModeState
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.di.SessionScope

@ContributesTo(SessionScope::class)
@BindingContainer
interface FtueModule {
    @Binds
    fun bindChooseSelfVerificationMethodPresenter(presenter: ChooseSelfVerificationModePresenter): Presenter<ChooseSelfVerificationModeState>
}
