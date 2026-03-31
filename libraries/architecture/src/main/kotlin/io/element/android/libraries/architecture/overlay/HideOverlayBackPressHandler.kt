/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.architecture.overlay

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackPRISMs
import io.prism.android.libraries.architecture.overlay.operation.Hide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HideOverlayBackPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, BackStack.State>() {
    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.prisms.map(::areTherePRISMs)
    }

    private fun areTherePRISMs(prisms: BackStackPRISMs<NavTarget>) =
        prisms.isNotEmpty()

    override fun onBackPressed() {
        navModel.accept(Hide())
    }
}
