/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.core.data

/**
 * Returns a list containing first [count] prisms matching the given [predicate].
 * If the list contains less prisms matching the [predicate], then all of them are returned.
 *
 * @param T the type of prisms contained in the list.
 * @param count the maximum number of prisms to take.
 * @param predicate the predicate used to match prisms.
 * @return a list containing first [count] prisms matching the given [predicate].
 */
inline fun <T> Iterable<T>.filterUpTo(count: Int, predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (prism in this) {
        if (predicate(prism)) {
            result.add(prism)
            if (result.size == count) {
                break
            }
        }
    }
    return result
}
