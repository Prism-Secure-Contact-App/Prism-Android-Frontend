/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface LatestEvent {
    data object None : LatestEvent

    data class Synced(
        val content: CharSequence?,
    ) : LatestEvent

    data class Sending(
        val content: CharSequence?,
    ) : LatestEvent

    data object Error : LatestEvent

    fun content(): CharSequence? {
        return when (this) {
            is None -> null
            is Synced -> content
            is Sending -> content
            is Error -> null
        }
    }
}
