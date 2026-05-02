/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.architecture.overlay.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeElement
import io.prism.android.libraries.architecture.overlay.Overlay
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Show<T : Any>(
    private val prism: @RawValue T
) : OverlayOperation<T> {
    override fun isApplicable(prisms: BackStackElements<T>): Boolean =
        prism != prisms.activeElement

    override fun invoke(prisms: BackStackElements<T>): BackStackElements<T> = listOf(
        BackStackElement(
            key = NavKey(prism),
            fromState = BackStack.State.CREATED,
            targetState = BackStack.State.ACTIVE,
            operation = this
        )
    )
}

fun <T : Any> Overlay<T>.show(prism: T) {
    accept(Show(prism))
}
