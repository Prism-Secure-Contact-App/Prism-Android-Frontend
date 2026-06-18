/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.loginpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import dev.zacsweers.metro.Inject
import io.prism.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.prism.android.libraries.architecture.AsyncData
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.matrix.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class LoginPasswordPresenter(
    private val authenticationService: PRISMAuthenticationService,
    private val accountProviderDataSource: AccountProviderDataSource,
) : Presenter<LoginPasswordState> {
    @Composable
    override fun present(): LoginPasswordState {
        val localCoroutineScope = rememberCoroutineScope()
        val loginAction: MutableState<AsyncData<SessionId>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        val formState = remember {
            mutableStateOf(LoginFormState.Default)
        }
        val accountProvider by accountProviderDataSource.flow.collectAsState()

        fun handleEvent(event: LoginPasswordEvents) {
            when (event) {
                is LoginPasswordEvents.SetLogin -> updateFormState(formState) {
                    copy(login = event.login)
                }
                is LoginPasswordEvents.SetPassword -> updateFormState(formState) {
                    copy(password = event.password)
                }
                LoginPasswordEvents.Submit -> {
                    localCoroutineScope.submit(formState.value, loginAction, accountProvider.url)
                }
                LoginPasswordEvents.ClearError -> loginAction.value = AsyncData.Uninitialized
            }
        }

        return LoginPasswordState(
            accountProvider = accountProvider,
            formState = formState.value,
            loginAction = loginAction.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.submit(
        formState: LoginFormState,
        loggedInState: MutableState<AsyncData<SessionId>>,
        homeserverUrl: String,
    ) = launch {
        loggedInState.value = AsyncData.Loading()
        authenticationService.setHomeserver(homeserverUrl)
            .map {
                authenticationService.login(formState.login.trim(), formState.password).getOrThrow()
            }
            .onSuccess { sessionId ->
                loggedInState.value = AsyncData.Success(sessionId)
            }
            .onFailure { failure ->
                loggedInState.value = AsyncData.Failure(failure)
            }
    }

    private fun updateFormState(formState: MutableState<LoginFormState>, updateLambda: LoginFormState.() -> LoginFormState) {
        formState.value = updateLambda(formState.value)
    }
}
