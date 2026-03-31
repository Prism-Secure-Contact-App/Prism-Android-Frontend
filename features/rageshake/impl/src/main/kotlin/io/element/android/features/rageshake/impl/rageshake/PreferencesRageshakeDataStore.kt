/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.rageshake.impl.rageshake

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.libraries.core.bool.orFalse
import io.prism.android.libraries.preferences.api.store.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val enabledKey = booleanPreferencesKey("enabled")
private val sensitivityKey = floatPreferencesKey("sensitivity")

@ContributesBinding(AppScope::class)
class PreferencesRageshakeDataStore(
    preferenceDataStoreFactory: PreferenceDataStoreFactory,
) : RageshakeDataStore {
    private val store = preferenceDataStoreFactory.create("prismx_rageshake")

    override fun isEnabled(): Flow<Boolean> {
        return store.data.map { prefs ->
            prefs[enabledKey].orFalse()
        }
    }

    override suspend fun setIsEnabled(isEnabled: Boolean) {
        store.edit { prefs ->
            prefs[enabledKey] = isEnabled
        }
    }

    override fun sensitivity(): Flow<Float> {
        return store.data.map { prefs ->
            prefs[sensitivityKey] ?: 0.5f
        }
    }

    override suspend fun setSensitivity(sensitivity: Float) {
        store.edit { prefs ->
            prefs[sensitivityKey] = sensitivity
        }
    }

    override suspend fun reset() {
        store.edit { it.clear() }
    }
}
