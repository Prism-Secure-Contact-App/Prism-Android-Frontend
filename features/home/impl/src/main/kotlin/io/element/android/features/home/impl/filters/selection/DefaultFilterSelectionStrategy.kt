/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.home.impl.filters.selection

import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.home.impl.filters.RoomListFilter
import io.prism.android.libraries.di.SessionScope
import kotlinx.coroutines.flow.MutableStateFlow

@ContributesBinding(SessionScope::class)
class DefaultFilterSelectionStrategy : FilterSelectionStrategy {
    private val selectedFilters = LinkedHashSet<RoomListFilter>()
    private val availableFilters
        get() = RoomListFilter.entries.toSet()

    override val filterSelectionStates = MutableStateFlow(buildFilters())

    override fun select(filter: RoomListFilter) {
        selectedFilters.add(filter)
        filterSelectionStates.value = buildFilters()
    }

    override fun deselect(filter: RoomListFilter) {
        selectedFilters.remove(filter)
        filterSelectionStates.value = buildFilters()
    }

    override fun isSelected(filter: RoomListFilter): Boolean {
        return selectedFilters.contains(filter)
    }

    override fun clear() {
        selectedFilters.clear()
        filterSelectionStates.value = buildFilters()
    }

    private fun buildFilters(): Set<FilterSelectionState> {
        val selectedFilterStates = selectedFilters.map {
            FilterSelectionState(
                filter = it,
                isSelected = true
            )
        }
        val unselectedFilters = availableFilters - selectedFilters - selectedFilters.flatMap { it.incompatibleFilters }.toSet()
        val unselectedFilterStates = unselectedFilters.map {
            FilterSelectionState(
                filter = it,
                isSelected = false
            )
        }
        return (selectedFilterStates + unselectedFilterStates).toSet()
    }
}
