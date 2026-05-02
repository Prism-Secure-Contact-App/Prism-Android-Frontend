/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.appnav.root

import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import dev.zacsweers.metro.Inject
import io.prism.android.appnav.di.PRISMSessionCache
import io.prism.android.features.preferences.api.CacheService
import io.prism.android.libraries.matrix.ui.media.ImageLoaderHolder
import io.prism.android.libraries.preferences.api.store.SessionPreferencesStoreFactory
import io.prism.android.libraries.sessionstorage.api.SessionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

private const val SAVE_INSTANCE_KEY = "io.prism.android.x.RootNavStateFlowFactory.SAVE_INSTANCE_KEY"

/**
 * This class is responsible for creating a flow of [RootNavState].
 * It gathers data from multiple datasource and creates a unique one.
 */
@Inject
class RootNavStateFlowFactory(
    private val sessionStore: SessionStore,
    private val cacheService: CacheService,
    private val prismSessionCache: PRISMSessionCache,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val sessionPreferencesStoreFactory: SessionPreferencesStoreFactory,
) {
    private var currentCacheIndex = 0

    fun create(savedStateMap: SavedStateMap?): Flow<RootNavState> {
        return combine(
            cacheIndexFlow(savedStateMap),
            sessionStore.loggedInStateFlow(),
        ) { cacheIndex, loggedInState ->
            RootNavState(
                cacheIndex = cacheIndex,
                loggedInState = loggedInState,
            )
        }
    }

    fun saveIntoSavedState(stateMap: MutableSavedStateMap) {
        stateMap[SAVE_INSTANCE_KEY] = currentCacheIndex
    }

    /**
     * @return a flow of integer, where each time a clear cache is done, we have a new incremented value.
     */
    private fun cacheIndexFlow(savedStateMap: SavedStateMap?): Flow<Int> {
        val initialCacheIndex = savedStateMap.getCacheIndexOrDefault()
        return cacheService.clearedCacheEventFlow
            .onEach { sessionId ->
                prismSessionCache.remove(sessionId)
                // Ensure image loader will be recreated with the new PRISMClient
                imageLoaderHolder.remove(sessionId)
                // Also remove cached value for SessionPreferencesStore
                sessionPreferencesStoreFactory.remove(sessionId)
            }
            .toIndexFlow(initialCacheIndex)
            .onEach { cacheIndex ->
                currentCacheIndex = cacheIndex
            }
    }

    /**
     * @return a flow of integer that increments the value by one each time a new prism is emitted upstream.
     */
    private fun Flow<Any>.toIndexFlow(initialValue: Int): Flow<Int> = flow {
        var index = initialValue
        emit(initialValue)
        collect {
            emit(++index)
        }
    }

    private fun SavedStateMap?.getCacheIndexOrDefault(): Int {
        return this?.get(SAVE_INSTANCE_KEY) as? Int ?: 0
    }
}
