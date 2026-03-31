/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.lockscreen.impl.unlock.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.Inject
import io.prism.android.compound.colors.SemanticColorsLightDark
import io.prism.android.features.enterprise.api.EnterpriseService
import io.prism.android.features.lockscreen.api.LockScreenLockState
import io.prism.android.features.lockscreen.api.LockScreenService
import io.prism.android.features.lockscreen.impl.unlock.PinUnlockPresenter
import io.prism.android.features.lockscreen.impl.unlock.PinUnlockView
import io.prism.android.features.lockscreen.impl.unlock.di.PinUnlockBindings
import io.prism.android.libraries.architecture.bindings
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.designsystem.theme.PRISMThemeApp
import io.prism.android.libraries.preferences.api.store.AppPreferencesStore
import kotlinx.coroutines.launch

class PinUnlockActivity : AppCompatActivity() {
    internal companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PinUnlockActivity::class.java)
        }
    }

    @Inject lateinit var presenter: PinUnlockPresenter
    @Inject lateinit var lockScreenService: LockScreenService
    @Inject lateinit var appPreferencesStore: AppPreferencesStore
    @Inject lateinit var enterpriseService: EnterpriseService
    @Inject lateinit var buildMeta: BuildMeta

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        bindings<PinUnlockBindings>().inject(this)
        setContent {
            val colors by remember {
                enterpriseService.semanticColorsFlow(sessionId = null)
            }.collectAsState(SemanticColorsLightDark.default)
            PRISMThemeApp(
                appPreferencesStore = appPreferencesStore,
                compoundLight = colors.light,
                compoundDark = colors.dark,
                buildMeta = buildMeta,
            ) {
                val state = presenter.present()
                PinUnlockView(
                    state = state,
                    isInAppUnlock = false,
                )
            }
        }
        lifecycleScope.launch {
            lockScreenService.lockState.collect { state ->
                if (state == LockScreenLockState.Unlocked) {
                    finish()
                }
            }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
