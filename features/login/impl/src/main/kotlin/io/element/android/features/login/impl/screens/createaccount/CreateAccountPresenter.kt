/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.createaccount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.appconfig.AuthenticationConfig
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.core.extensions.flatMap
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.core.meta.BuildMeta
import io.prism.android.libraries.matrix.api.auth.PRISMAuthenticationService
import io.prism.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AssistedInject
class CreateAccountPresenter(
    @Assisted private val url: String,
    private val authenticationService: PRISMAuthenticationService,
    private val messageParser: MessageParser,
    private val buildMeta: BuildMeta,
) : Presenter<CreateAccountState> {
    @AssistedFactory
    interface Factory {
        fun create(url: String): CreateAccountPresenter
    }

    @Composable
    override fun present(): CreateAccountState {
        val coroutineScope = rememberCoroutineScope()
        val pageProgress = remember { mutableIntStateOf(0) }
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val passwordConfirm = remember { mutableStateOf("") }
        val createAction = remember { mutableStateOf<AsyncAction<SessionId>>(AsyncAction.Uninitialized) }

        val isSubmitEnabled = username.value.isNotBlank() && 
                             password.value.isNotBlank() && 
                             password.value == passwordConfirm.value

        fun handleEvent(event: CreateAccountEvents) {
            when (event) {
                is CreateAccountEvents.SetUsername -> username.value = event.username
                is CreateAccountEvents.SetPassword -> password.value = event.password
                is CreateAccountEvents.SetPasswordConfirm -> passwordConfirm.value = event.passwordConfirm
                is CreateAccountEvents.Submit -> {
                    coroutineScope.registerDirectly(
                        username = username.value,
                        password = password.value,
                        loggedInState = createAction,
                    )
                }
                is CreateAccountEvents.SetPageProgress -> {
                    pageProgress.intValue = event.progress
                }
                is CreateAccountEvents.OnMessageReceived -> {
                    if (event.message.contains("isTrusted")) return
                    coroutineScope.importSession(event.message, createAction)
                }
            }
        }

        return CreateAccountState(
            url = url,
            username = username.value,
            password = password.value,
            passwordConfirm = passwordConfirm.value,
            isSubmitEnabled = isSubmitEnabled,
            pageProgress = pageProgress.intValue,
            isDebugBuild = buildMeta.isDebuggable,
            createAction = createAction.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.importSession(message: String, loggedInState: MutableState<AsyncAction<SessionId>>) = launch {
        loggedInState.value = AsyncAction.Loading
        runCatchingExceptions {
            messageParser.parse(message)
        }.flatMap { externalSession ->
            authenticationService.importCreatedSession(externalSession)
        }.onSuccess { sessionId ->
            loggedInState.value = AsyncAction.Success(sessionId)
        }.onFailure { failure ->
            loggedInState.value = AsyncAction.Failure(failure)
        }
    }

    /**
     * v1.0.0 in-app registration. Posts directly to Synapse `/_matrix/client/v3/register`
     * (hard-coded `AuthenticationConfig.PRISM_ORG_URL`) with username + password and the
     * `m.login.dummy` auth stage — no e-mail, SMS, captcha, or WebView. The returned access
     * token is imported into the SDK so the user lands on the FTUE wizard immediately,
     * just like a fresh login. 2FA / e-mail verification is deferred to v1.0.1.
     */
    private fun CoroutineScope.registerDirectly(
        username: String,
        password: String,
        loggedInState: MutableState<AsyncAction<SessionId>>,
    ) = launch {
        loggedInState.value = AsyncAction.Loading
        // Synapse expects the localpart only; strip any leading "@" or "#" the user typed.
        val localpart = username.trim().removePrefix("@").substringBefore(':')
        // The Rust SDK refuses to import a foreign session unless an authentication context
        // for the homeserver has been initialised first ("You need to call 'setHomeserver()' first").
        // We bind to the hardcoded PRISM homeserver before issuing the registration POST.
        authenticationService.setHomeserver(AuthenticationConfig.PRISM_ORG_URL)
            .flatMap {
                SynapseRegisterClient(homeserverUrl = AuthenticationConfig.PRISM_ORG_URL)
                    .register(localpart, password)
            }
            .flatMap { externalSession ->
                authenticationService.importCreatedSession(externalSession)
            }
            .onSuccess { sessionId ->
                loggedInState.value = AsyncAction.Success(sessionId)
            }
            .onFailure { failure ->
                loggedInState.value = AsyncAction.Failure(failure)
            }
    }
}
