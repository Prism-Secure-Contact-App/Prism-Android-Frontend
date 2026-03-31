/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding.classic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.featureflag.api.FeatureFlagService
import io.prism.android.libraries.featureflag.api.FeatureFlags
import io.prism.android.libraries.sessionstorage.api.SessionStore
import io.prism.android.libraries.sessionstorage.api.toUserListFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Inject
class LoginWithClassicPresenter(
    private val prismClassicConnection: PRISMClassicConnection,
    private val sessionStore: SessionStore,
    private val featureFlagService: FeatureFlagService,
) : Presenter<LoginWithClassicState> {
    @Composable
    override fun present(): LoginWithClassicState {
        val coroutineScope = rememberCoroutineScope()

        val isSignInWithClassicEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.SignInWithClassic)
        }.collectAsState(initial = false)

        if (isSignInWithClassicEnabled) {
            DisposableEffect(Unit) {
                prismClassicConnection.start()
                onDispose {
                    prismClassicConnection.stop()
                }
            }
        }

        val state by prismClassicConnection.stateFlow.collectAsState()
        val loginWithClassicAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        val existingSession by remember {
            sessionStore.sessionsFlow().toUserListFlow()
        }.collectAsState(emptyList())

        val canLoginWithClassic by remember {
            derivedStateOf {
                when (val finalState = state) {
                    is PRISMClassicConnectionState.PRISMClassicReady -> {
                        // Ensure there is no existing session with the same Id.
                        finalState.userId.value !in existingSession && isSignInWithClassicEnabled
                    }
                    else -> false
                }
            }
        }

        fun handleEvent(event: LoginWithClassicEvent) {
            when (event) {
                LoginWithClassicEvent.RefreshData -> {
                    prismClassicConnection.requestData()
                }
                LoginWithClassicEvent.StartLoginWithClassic -> {
                    val currentState = prismClassicConnection.stateFlow.value
                    if (currentState is PRISMClassicConnectionState.PRISMClassicReady) {
                        loginWithClassicAction.value = ConfirmingLoginWithPRISMClassic(
                            userId = currentState.userId,
                        )
                    } else {
                        loginWithClassicAction.value = AsyncAction.Failure(IllegalStateException("PRISM Classic is not ready"))
                    }
                }
                LoginWithClassicEvent.DoLoginWithClassic -> coroutineScope.launch {
                    // TODO Implement real login logic here
                    loginWithClassicAction.value = AsyncAction.Loading
                    delay(1000)
                    loginWithClassicAction.value = AsyncAction.Success(Unit)
                }
                LoginWithClassicEvent.CloseDialog -> {
                    loginWithClassicAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return LoginWithClassicState(
            canLoginWithClassic = canLoginWithClassic,
            loginWithClassicAction = loginWithClassicAction.value,
            eventSink = ::handleEvent,
        )
    }
}
