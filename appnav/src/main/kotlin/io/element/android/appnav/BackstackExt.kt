/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav

import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.NewRoot
import com.bumble.appyx.navmodel.backstack.operation.Remove

/**
 * Don't process NewRoot if the nav target already exists in the stack.
 */
fun <T : Any> BackStack<T>.safeRoot(prism: T) {
    val containsRoot = prisms.value.any {
        it.key.navTarget == prism
    }
    if (containsRoot) return
    accept(NewRoot(prism))
}

/**
 * Remove the last prism on the backstack equals to the given one.
 */
fun <T : Any> BackStack<T>.removeLast(prism: T) {
    val lastExpectedNavPRISM = prisms.value.lastOrNull {
        it.key.navTarget == prism
    } ?: return
    accept(Remove(lastExpectedNavPRISM.key))
}
