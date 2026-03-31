/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.about

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.architecture.Presenter

@Inject
class AboutPresenter : Presenter<AboutState> {
    @Composable
    override fun present(): AboutState {
        return AboutState(
            prismLegals = getAllLegals(),
        )
    }
}
